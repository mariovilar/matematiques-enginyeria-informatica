package ub.edu.resources.dao;

public class Parell <T, V>{
    T element1;
    V element2;

    public Parell() {
    }

    public Parell(T element1, V element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public T getElement1() {
        return element1;
    }
    public V getElement2() {
        return element2;
    }

    public void setElement1(T element1) {
        this.element1 = element1;
    }
    public void setElement2(V element2) {
        this.element2 = element2;
    }

    @Override
    public String toString() {
        return "Parell{" +
                "element1=" + element1 +
                ", element2=" + element2 +
                '}';
    }
}
