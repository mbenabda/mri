package org.mri;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class MethodNotFoundException extends Exception {
    private String methodName;

    public MethodNotFoundException(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodNotFoundException that = (MethodNotFoundException) o;
        return Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("methodName", methodName)
            .toString();
    }
}
