/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.sbt.jschool.session2;

import com.sun.org.apache.xpath.internal.operations.Number;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 */
public class OutputFormatter {
    private PrintStream out;

    public OutputFormatter(PrintStream out) {
        this.out = out;
    }

    public void output(String[] names, Object[][] data) {
        System.setOut(this.out);

        //TODO: implement me.
        //System.out.println(Arrays.deepToString(data));
        Builder table = new Builder(names, data);
        //this.out = drawTable(names,data);
    }
}

class Builder {
    int rows;
    int cols;
    int height_default = 1;
    int width_default = 1;
    int [] width;
    enum Align {LEFT, CENTER, RIGHT};

    Builder(String [] names, Object[][] data) {
        rows = data.length;
        cols = names.length;
        width = new int[names.length];

        // заполняем "нулями" width
        for (int i = 0; i < names.length; i++)
            width[i] = width_default;

        // устанавливаем максимальную ширину для всех names
        for (int i = 0; i < names.length; i++)
            if (names[i].length() > width[i])
                width[i] = names[i].length();

        // устанавливаем максимальную ширину для всех data
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++)
                if (this.cellLength(data[i][j]) > width[j])
                    width[j] = this.cellLength(data[i][j]);
        }
        this.drawTable(names, data);
    }

    int cellLength(Object obj) {
        if (obj instanceof String)
            return ((String) obj).length();
        else if (obj instanceof Date)
            return 10;
        else if (obj instanceof Float)
            return ((Float) obj).toString().length();
        else if (obj instanceof Double)
            return ((Double) obj).toString().length();
        else if (obj instanceof Integer)
            return ((Integer) obj).toString().length();
        return -1;
    }

    void drawTable(String[] names, Object[][] data) {
        drawRowLine();
        System.out.print("|");
        for (int i = 0; i < cols; i++) {
            drawCellText(names[i], i, Align.CENTER);
            System.out.print("|");
        }
        System.out.println();

        for (int i = 0; i < data.length; i++)
        {
            drawRowLine();
            System.out.print("|");
            for (int j = 0; j < cols; j++) {
                if (data[i][j] instanceof String)
                    drawCellText(convert(data[i][j]), j, Align.LEFT);
                else if ((data[i][j] instanceof Date) || (data[i][j] instanceof Float) || (data[i][j] instanceof Double) || (data[i][j] instanceof Number))
                    drawCellText(convert(data[i][j]), j, Align.RIGHT);
                System.out.print("|");
            }
            System.out.println();

        }
        drawRowLine();
    }

    void drawCellLine(int count) {
        for (int i = 0; i < count; i++)
                System.out.print("-");
    }

    void drawRowLine() {
        System.out.print("+");
        for (int i = 0; i < cols; i++) {
            drawCellLine(this.width[i]);
            System.out.print("+");
        }
        System.out.println();
    }

    String convert(Object obj) {
        String result = null;
        if (obj == null)
            return "-";
        else if (obj instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            result = dateFormat.format((Date) obj);
        }
        else if (obj instanceof Float) {
            DecimalFormat floatFormat = new DecimalFormat("###,##0.00");
            result = floatFormat.format((Float) obj);
        }
        else if (obj instanceof Double) {
            DecimalFormat doubleFormat = new DecimalFormat("###,##0.00");
            result = doubleFormat.format((Double) obj);
        }
        else if (obj instanceof Number) {
            DecimalFormat numberFormat = new DecimalFormat("###.###");
            result = numberFormat.format((Number) obj);
        }
        return result;
    }

    void drawCellText(String source, int index, Align align) {
        if (source == null) {
            align = Align.RIGHT;
        }
        else if (align == Align.CENTER) {
            int skip = (this.width[index] - source.length()) / 2;
            for (int i = 0; i < skip; i++)
                System.out.print(" ");
            System.out.print(source);
            for (int i = 0; i < this.width[index] - source.length() - skip; i++)
                System.out.print(" ");
        }
        else if (align == Align.RIGHT)
        {
            for (int i = 0; i < this.width[index] - source.length(); i++)
                System.out.print(" ");
            System.out.print(source);
        }
        else if (align == Align.LEFT)
        {
            System.out.print(source);
            for (int i = 0; i < this.width[index] - source.length(); i++)
                System.out.print(" ");
        }
    }
}
