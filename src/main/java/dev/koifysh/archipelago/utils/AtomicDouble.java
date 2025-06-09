package dev.koifysh.archipelago.utils;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleUnaryOperator;

public class AtomicDouble {
    // Cause Java hates us, and doesn't want to give us doubles

    private final AtomicLong bits = new AtomicLong();

    public AtomicDouble(double initialValue)
    {
        bits.set(Double.doubleToLongBits(initialValue));
    }

    public double get()
    {
        return Double.longBitsToDouble(bits.get());
    }

    public void set(double value)
    {
        bits.set(Double.doubleToLongBits(value));
    }

    public double getAndUpdate(DoubleUnaryOperator func)
    {
        return Double.longBitsToDouble(bits.getAndUpdate(l -> Double.doubleToLongBits(func.applyAsDouble(Double.longBitsToDouble(l)))));
    }

}
