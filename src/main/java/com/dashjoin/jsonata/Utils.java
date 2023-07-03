package com.dashjoin.jsonata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import com.dashjoin.jsonata.Jsonata.JFunction;

public class Utils {
    public static boolean isNumeric(Object v) throws JException {
        boolean isNum = false;
        if (v instanceof Number) {
            double d = ((Number)v).doubleValue();
            isNum = !Double.isNaN(d);
            if (isNum && !Double.isFinite(d)) {
                throw new JException("D1001", 0, v);
            }
        }
        return isNum;
    }

    public static boolean isArrayOfStrings(Object v) {
        boolean result = false;
        if (v instanceof Collection) {
            for (Object o : ((Collection)v))
                if (!(o instanceof String))
                    return false;
            return true;
        }
        return false;
    }
    public static boolean isArrayOfNumbers(Object v) throws JException {
        boolean result = false;
        if (v instanceof Collection) {
            for (Object o : ((Collection)v))
                if (!isNumeric(o))
                    return false;
            return true;
        }
        return false;
    }

    public static boolean isFunction(Object o) {
        return o instanceof JFunction;
    }

    static Object NONE = new Object();

    /**
     * Create an empty sequence to contain query results
     * @returns {Array} - empty sequence
     */
    public static List<Object> createSequence() { return createSequence(NONE); }

    public static List<Object> createSequence(Object el) {
        JList<Object> sequence = new JList<>();
        sequence.sequence = true;
        if (el!=NONE) {
            sequence.add(el);
        }
        return sequence;
    }

    public static class JList<E> extends ArrayList<E> {
        public JList() { super(); }
        public JList(int capacity) { super(capacity); }
        public JList(Collection<? extends E> c) {
            super(c);
        }

        // Jsonata specific flags
        public boolean sequence;

        public boolean outerWrapper;

        public boolean tupleStream;

        public boolean keepSingleton;

        public boolean cons;
        
        public String toString() {
          Iterator<E> it = iterator();
          if (! it.hasNext())
              return "[]";

          StringBuilder sb = new StringBuilder();
          sb.append('[');
          for (;;) {
              E e = it.next();
              sb.append(e == this ? "(this Collection)" : e);
              if (! it.hasNext())
                  return sb.append(']').toString();
              sb.append(',') /* .append(' ') */;
          }
        }
    }

    public static boolean isSequence(Object result) {
        return result instanceof JList && ((JList)result).sequence;
    }

        // createSequence,
        // isSequence,
        // isFunction,
        // isLambda,
        // isIterable,
        // getFunctionArity,
        // isDeepEqual,
        // stringToArray,
        // isPromise

     
    public static Number convertNumber(Number n) throws JException {
        // Use int if the number is not fractional
        if (!isNumeric(n)) return null;
        if (n.intValue()==n.doubleValue())
            return n.intValue();
        return n.doubleValue();
    }

}
