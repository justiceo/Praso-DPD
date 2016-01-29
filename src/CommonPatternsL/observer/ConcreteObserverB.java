package CommonPatternsL.observer;

/**
 * Created by Justice on 1/19/2016.
 */
public class ConcreteObserverB implements IObserver {
    @Override
    public void notifyObservers() {
        System.out.println("concrete observer B received the message");
    }
}
