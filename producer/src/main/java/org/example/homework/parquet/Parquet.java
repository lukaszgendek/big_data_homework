package org.example.homework.parquet;

import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.schema.Type;

import java.util.List;

public record Parquet(List<SimpleGroup> data, List<Type> schema) {
}