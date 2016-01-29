package CommonPatternsL.observer;

/**
 * Created by Justice on 1/19/2016.
 */
public class ConcreteObserverA implements IObserver {
    @Override
    public void notifyObservers() {
        System.out.println("concrete observer A received the message");
    }
}
