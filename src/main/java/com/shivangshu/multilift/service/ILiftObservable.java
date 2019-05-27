package com.shivangshu.multilift.service;

public interface ILiftObservable {

    void notifyObservers();

    void addLiftObservers(ILiftObserver liftObserver);
}
