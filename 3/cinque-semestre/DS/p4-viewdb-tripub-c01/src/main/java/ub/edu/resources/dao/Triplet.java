package ub.edu.resources.dao;

public class Triplet<T, V, W>{
    T element1;
    V element2;
    W element3;

    public Triplet() {
    }

    public Triplet(T element1, V element2, W element3) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
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

    public void setElement1(T element1) {
        this.element1 = element1;
    }
    public void setElement2(V element2) {
        this.element2 = element2;
    }
    public void setElement3(W element3) {
        this.element3 = element3;
    }

    @Override
    public String toString() {
        return "Triplet{" +
                "element1=" + element1 +
                ", element2=" + element2 +
                ", element3=" + element3 +
                '}';
    }
}
