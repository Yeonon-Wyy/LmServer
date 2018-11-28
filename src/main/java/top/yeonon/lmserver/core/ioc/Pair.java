package top.yeonon.lmserver.core.ioc;

import java.util.Objects;

/**
 *
 * 元组类
 *
 * 元组在不少语言中都有，例如Python，C++等，但Java中没有，不过自写也非常简单。
 * @Author yeonon
 * @date 2018/11/28 0028 17:40
 **/

public class Pair<T1,T2> {

    private T1 first;
    private T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public Pair() {
    }

    public T1 first() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    public T2 second() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {

        return Objects.hash(first, second);
    }
}
