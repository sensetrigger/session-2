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

//import com.sun.org.apache.xpath.internal.operations.Number;

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
    private int rows;
    private int cols;
    private int heightDefault = 1;
    private int widthDefault = 1;
    private int [] width;
    private enum Align {LEFT, CENTER, RIGHT};

    final private int cellLength(Object obj) {
        if (obj instanceof Date)
            return 10;
        else
            return convert(obj).toString().length();
    }

    private String convert(Object obj) {
        if (obj == null)
            return "-";
        else if (obj instanceof String)
            return (String) obj;
        else if (obj instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return dateFormat.format((Date) obj);
        }
        else if (obj instanceof Float) {
            DecimalFormat floatFormat = new DecimalFormat("###,##0.00");
            return floatFormat.format((Float) obj);
        }
        else if (obj instanceof Double) {
            DecimalFormat doubleFormat = new DecimalFormat("###,##0.00");
            return doubleFormat.format((Double) obj);
        }
        else if (obj instanceof Number) {
            DecimalFormat numberFormat = new DecimalFormat("###,###");
            return numberFormat.format((Number) obj);
        }
        return "-";
    }

    private void drawCellText(String source, int index, Align align) {
        if (align == Align.CENTER) {
            int skip = (this.width[index] - source.length()) / 2;
            for (int i = 0; i < skip; i++)
                this.out.print(" ");
            this.out.print(source);
            for (int i = 0; i < this.width[index] - source.length() - skip; i++)
                this.out.print(" ");
        }
        else if (align == Align.RIGHT) {
            for (int i = 0; i < this.width[index] - source.length(); i++)
                this.out.print(" ");
            this.out.print(source);
        }
        else if (align == Align.LEFT) {
            this.out.print(source);
            for (int i = 0; i < this.width[index] - source.length(); i++)
                this.out.print(" ");
        }
    }

    public OutputFormatter(PrintStream out) {
        this.out = out;
    }

    public void output(String[] names, Object[][] data) {
        this.rows = data.length;
        this.cols = names.length;
        this.width = new int[names.length];

        for (int i = 0; i < names.length; i++)
            this.width[i] = widthDefault;

        for (int i = 0; i < names.length; i++)
            if (names[i].length() > width[i])
                this.width[i] = names[i].length();

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++)
                if (this.cellLength(data[i][j]) > width[j])
                    this.width[j] = this.cellLength(data[i][j]);
        }

        this.out.print("+");
        for (int r = 0; r < cols; r++) {
            for (int c = 0; c < this.width[r]; c++)
                this.out.print("-");
            this.out.print("+");
        }
        this.out.println();
        this.out.print("|");
        for (int i = 0; i < cols; i++) {
            drawCellText(names[i], i, Align.CENTER);
            this.out.print("|");
        }
        this.out.println();

        for (int i = 0; i < data.length; i++) {
            this.out.print("+");
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < this.width[r]; c++)
                    this.out.print("-");
                this.out.print("+");
            }
            this.out.println();
            this.out.print("|");
            for (int j = 0; j < cols; j++) {
                if (data[i][j] == null) {
                    if ((i > 1) & data[i-1][j] instanceof String)
                        drawCellText(convert(data[i][j]), j, Align.LEFT);
                    else
                        drawCellText(convert(data[i][j]), j, Align.RIGHT);
                }
                else if (data[i][j] instanceof String)
                    drawCellText(convert(data[i][j]), j, Align.LEFT);
                else
                    drawCellText(convert(data[i][j]), j, Align.RIGHT);
                this.out.print("|");
            }
            this.out.println();

        }
        this.out.print("+");
        for (int r = 0; r < cols; r++) {
            for (int c = 0; c < this.width[r]; c++)
                this.out.print("-");
            this.out.print("+");
        }
        this.out.println();
    }
}
