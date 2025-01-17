package org.example.homework.parquet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.ColumnIOFactory;

import java.io.IOException;
import java.util.ArrayList;

public class ParquetReaderUtils {
    public static Parquet getParquetData(String filePath) throws IOException {
        var simpleGroups = new ArrayList<SimpleGroup>();
        var reader = ParquetFileReader.open(HadoopInputFile.fromPath(new Path(filePath), new Configuration()));
        var schema = reader.getFooter().getFileMetaData().getSchema();
        PageReadStore pages;
        while ((pages = reader.readNextRowGroup()) != null) {
            var rows = pages.getRowCount();
            var columnIO = new ColumnIOFactory().getColumnIO(schema);
            var recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));

            for (int i = 0; i < rows; i++) {
                var simpleGroup = (SimpleGroup) recordReader.read();
                simpleGroups.add(simpleGroup);
            }
        }
        reader.close();
        return new Parquet(simpleGroups, schema.getFields());
    }
}
