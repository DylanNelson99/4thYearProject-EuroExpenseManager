package com.project.euroexpensemanager.component;

import android.graphics.Color;
import android.widget.TableRow;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class PiaChart {
    private static List<PieEntry> getEntries(int online, int DiningOut, int Groceries, int Transport, int Bills) {
        List<PieEntry> pieEntries;
        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(online, 0));
        pieEntries.add(new PieEntry(DiningOut, 1));
        pieEntries.add(new PieEntry(Groceries, 2));
        pieEntries.add(new PieEntry(Transport, 3));
        pieEntries.add(new PieEntry(Bills, 4));
        pieEntries.get(0).setLabel("Online");
        pieEntries.get(1).setLabel("DiningOut");
        pieEntries.get(2).setLabel("Groceries");
        pieEntries.get(3).setLabel("Transport");
        pieEntries.get(4).setLabel("Bills");
        return pieEntries;
    }

		//Entries color list
    private static List<Integer> getColorsList() {
        ArrayList<Integer> colors;
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#e64b3b"));
        colors.add(Color.parseColor("#3397da"));
        colors.add(Color.parseColor("#2ecc70"));
        colors.add(Color.parseColor("#CA6525"));
        colors.add(Color.parseColor("#FFBB86FC"));
        return colors;
    }

			//Pie chart generator
    public static void attendancePiaChartGenerator(PieChart chart, int online, int DiningOut, int Groceries, int Transport, int Bills) {
        configurePiaChart(chart);
        chart.setData(getPiaData(online, DiningOut, Groceries, Transport, Bills));
    }

//Configuring of pie chart all below
    private static PieData getPiaData(int online, int DiningOut, int Groceries, int Transport, int Bills) {
        PieData pieData;
        PieDataSet pieDataSet;
        List<PieEntry> pieEntries = getEntries(online, DiningOut, Groceries, Transport, Bills);
        pieDataSet = new PieDataSet(pieEntries, "");
        pieData = new PieData(pieDataSet);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(10);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setColors(getColorsList());
        return pieData;
    }

    private static PieChart configurePiaChart(PieChart chart) {
        chart.animateX(2000, Easing.EaseInOutBounce);
        chart.setHoleRadius(50f);
        chart.setDrawSlicesUnderHole(true);
        chart.setTransparentCircleRadius(60f);
        chart.setDrawEntryLabels(true);
        chart.setEntryLabelTextSize(8);
        chart.setCenterText("Total\nTransactions");
        chart.setCenterTextSize(15);
        Description description = chart.getDescription();
        description.setText("");
        chart.setDescription(description);
        chart.getLegend().setEnabled(false);
        return chart;
    }
}
