package ub.edu.resources.dao;

public class Quartet<T, V, W, X>{
    T element1;
    V element2;
    W element3;
    X element4;

    public Quartet() {
    }

    public Quartet(T element1, V element2, W element3, X element4) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
        this.element4 = element4;
    }

    public T getElement1() {
        return element1;
    }
    public V getElement2() {
        return element2;
    }
    public W getElement3() {
        return element3;
    }
    public X getElement4() {
        return element4;
    }

    public void setElement1(T element1) {
        this.element1 = element1;
    }
    public void setElement2(V element2) {
        this.element2 = element2;
    }
    public void setElement3(W element3) {
        this.element3 = element3;
    }
    public void setElement4(X element4) {
        this.element4 = element4;
    }

    @Override
    public String toString() {
        return "Quartet{" +
                "element1=" + element1 +
                ", element2=" + element2 +
                ", element3=" + element3 +
                ", element4=" + element4 +
                '}';
    }
}
