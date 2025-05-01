//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package utils;

/**
 * Allows to build complex JSON strings with any objects which implements this interface
 */
public interface JSONBuilderAware {
    JSONBuilder addItem();
}
