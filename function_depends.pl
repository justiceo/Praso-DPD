
# Create a csv list for all function dependencies
#
# Usage:
sub usage($) {
    return shift(@_) . <<"END_USAGE";
Usage: $0 -db database
  -db database        Specify Understand database (required for
                      uperl, inherited from Understand)
  -f outputFile.csv   Specify the output file
END_USAGE
}
#
#  For the latest Understand perl API documentation, see
#      http://www.scitools.com/perl.html
#
#  15-Nov-2006 KG

use Understand;
use Getopt::Long;
use strict;

my $dbPath;
my $help;
my $output;
GetOptions(
     "db=s" => \$dbPath,
     "help" => \$help,
     "f=s"  => \$output,
          );

# help message
die usage("") if ($help);

die usage("Please specify output file\n") unless ($output);

# open the database
my $db=openDatabase($dbPath);

# check language
if ( $db->language() !~ "C|Java" ) {
    die "This script is designed for C and Java only\n";
}


#code body*******************************************************************
open(FILE,">$output") || die("Couldn't write to output file. $!");

#Get List of function
my @ents = $db->ents("function ~unknown ~unresolved, method ~unknown ~unresolved");

#print header
print FILE "Function, File, Line, DependsOnEnt,DependsOnType,DependsOnFile\n";

foreach my $function (sort {lc($a->longname()) cmp lc($b->longname());} @ents){
  #Get the file where the function is defined
  my $defFileRef = getDeclRef($function);
  next unless $defFileRef;
  my $file = $defFileRef->file;
  
  #Get all the dependency refrences from the file
  my $fileDeps = $file->depends();
  next unless $fileDeps;
  my @refs = $fileDeps->values();
  
  foreach my $ref (@refs){
    next unless $ref->scope->id eq $function->id; #Only show references from the function
    
    my $depDecl = getDeclRef($ref->ent); #get the declaration references for the dependent object
    
    #Print out the dependency info
    print FILE $function->longname."(".$function->parameters."),";
    print FILE $ref->file->longname.",";
    print FILE $ref->line.",";
    print FILE $ref->ent->name.",";
    print FILE $ref->kindname().",";
    print FILE $depDecl->file->longname if $depDecl;
    print FILE "\n";
  }
  print ".";  
}
print "\nFinished\n";

#end body********************************************************************
closeDatabase($db);


# subroutines

sub openDatabase($)
{
    my ($dbPath) = @_;

    my $db = Understand::Gui::db();

    # path not allowed if opened by understand
    if ($db&&$dbPath) {
  die "database already opened by GUI, don't use -db option\n";
    }

    # open database if not already open
    if (!$db) {
  my $status;
  die usage("Error, database not specified\n\n") unless ($dbPath);
  ($db,$status)=Understand::open($dbPath);
  die "Error opening database: ",$status,"\n" if $status;
    }
    return($db);
}

sub closeDatabase($)
{
    my ($db)=@_;

    # close database only if we opened it
    $db->close() if $dbPath;
}

# Pass in percent complete (decimal value) to update a progress bar in the GUI or command line
sub progress{
  my $percent = shift;
  if(Understand::Gui::db()){
    Understand::Gui::progress_bar($percent);
    Understand::Gui::yield();
  }else{
    print "Progress: ".int($percent*100)."%\r";
  }
}

# return declaration ref (based on language) or 0 if unknown

sub getDeclRef
{
   my ($ent) =@_;
   my @decl=();
   return @decl unless defined ($ent);
   my $language = $ent->language();
   if ($language =~ /ada/i)
   {
      my @declOrder = ("declarein ~spec ~body ~instance ~formal ~incomplete ~private ~stub",
                 "spec declarein",
                 "body declarein",
                 "instance declarein",
                 "formal declarein",
                 "incomplete declarein",
                 "private declarein",
                 "stub declarein");
               
      foreach my $type (@declOrder)
      {
         @decl = $ent->refs($type);
         if (@decl) { last;  }
      }
   }
   elsif ($language =~ /fortran/i)
   {
      @decl = $ent->refs("definein");
      if (!@decl) {   @decl = $ent->refs("declarein");  }
   }
   else # C/C++
   {
      @decl = $ent->refs("definein");
      if (!@decl) {   @decl = $ent->refs("declarein"); }
   }
   
   if (@decl)
   {   return $decl[0];   }
   else
   {   return 0;   }
}
