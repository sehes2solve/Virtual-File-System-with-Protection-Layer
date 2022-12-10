package com.company;


import java.io.Serializable;

class pair<T, C> implements Serializable
{
    T first;
    C second;
    pair(){this.first = null; this.second = null;}

    pair(T first, C second)
    {
        this.first = first;
        this.second = second;
    }
}
