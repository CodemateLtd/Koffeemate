package com.codemate.brewflop.ui.base

open class BasePresenter<T: MvpView> : Presenter<T> {
    private var mvpView: T? = null

    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        mvpView = null
    }

    fun isViewAttached(): Boolean {
        return mvpView != null
    }

    fun getView(): T? {
        return mvpView
    }

    fun ensureViewIsAttached() {
        if (!isViewAttached()) {
            throw ViewNotAttachedException()
        }
    }

    class ViewNotAttachedException : RuntimeException("View not attached! Please call attachView() first.")
}