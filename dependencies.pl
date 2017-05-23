
# Sample Understand PERL API Script
#
# Synopsis: Show all dependencies
#
# Language: C/C++, Java
#
# Usage:
sub usage($) {
    return shift(@_) . <<"END_USAGE";
Usage: $0 -db database
  -db database      Specify Understand database (required for
                    uperl, inherited from Understand)
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
GetOptions(
     "db=s" => \$dbPath,
     "help" => \$help,
          );

# help message
die usage("") if ($help);

# open the database
my $db=openDatabase($dbPath);

# check language
if ( $db->language() !~ "C|Java" ) {
    die "This script is designed for C and Java only\n";
}


#code body*******************************************************************

my @ents = $db->ents("function,method,file");
foreach my $ent (sort {lc($a->longname()) cmp lc($b->longname());} @ents){         
  my $dependsby = $ent->dependsby;
  my $depends = $ent->depends;
  next unless $depends || $dependsby;
  print $ent->name . "\n";
  #Depends
  
  if ($depends){
  print "Depends" . "\n";
  foreach my $dep($depends->keys()){
      print "\t".$dep->relname."\n";
      foreach my $defref($depends->value($dep)){
        print "\t\t".$defref->kindname()." of ".$defref->ent->name. " at line ".$defref->line."\n";
      }
  }
  }
  
  #Dependsby
  
  next unless $dependsby;
  print "Dependsby" . "\n";
  foreach my $depby($dependsby->keys()){
      print "\t".$depby->relname."\n";
      foreach my $defref($dependsby->value($depby)){
        print "\t\t".$defref->kindname()." of ".$defref->ent->name. " at line ".$defref->line."\n";
      }
    }
}

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
