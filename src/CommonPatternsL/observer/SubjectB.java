package CommonPatternsL.observer;

import java.util.List;

/**
 * Created by Justice on 1/19/2016.
 */
public class SubjectB {

    List<IObserver> observerList;

    public void registerObserver(IObserver observer) {
        observerList.add(observer);
    }

    public void notifyObservers(IObserver observer) {
        observerList.forEach(o -> o.notifyObservers());
    }

    public void detachObserver(IObserver observer) {
        observerList.remove(observer);
    }

}
