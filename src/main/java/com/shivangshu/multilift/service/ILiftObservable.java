package com.shivangshu.multilift.service;

public interface ILiftObservable {

    void notifyObservers(ILiftObserver liftObserver);

    void noticeChange();

    void addLiftObservers(ILiftObserver liftObserver);
}
