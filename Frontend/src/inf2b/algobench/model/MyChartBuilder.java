/*
 * Created by Yufen Wang.
 * 2016
 */


package inf2b.algobench.model;

import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.internal.style.Theme;

/**
 *
 * @author Yufen Wang
 */
public class MyChartBuilder extends ChartBuilder{
    
    StyleManager.ChartType chartType;
    int width;
    int height;
    String title;
    String xAxisTitle;
    String yAxisTitle;
    StyleManager.ChartTheme chartTheme;
    
    @Override
    public MyChart build(){
        return new MyChart(this);
    }
    
    @Override
    public MyChartBuilder chartType(StyleManager.ChartType chartType) {
        this.chartType = chartType;
        return this;
    }

    @Override
    public MyChartBuilder width(int width) {
        this.width = width;
        return this;
    }

    @Override
    public MyChartBuilder height(int height) {
        this.height = height;
        return this;
    }

    @Override
    public MyChartBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public MyChartBuilder xAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
        return this;
    }

    @Override
    public MyChartBuilder yAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
        return this;
    }

    @Override
    public MyChartBuilder theme(StyleManager.ChartTheme chartTheme) {
        this.chartTheme = chartTheme;
        return this;
    }
    
    public StyleManager.ChartTheme getTheme(){
        return this.chartTheme;
    }
}
