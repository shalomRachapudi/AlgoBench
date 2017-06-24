/*
 * Created by Yufen Wang.
 * 2016
 */

package inf2b.algobench.model;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;

/**
 *
 * @author Yufen WANG
 */
public class MyChart extends Chart{
    
    private Map<String, Series> All_series = new LinkedHashMap<>();
    
    public MyChart(int width, int height) {
        super(width, height);
    }
    
    public MyChart(MyChartBuilder builder){
        super(builder.width,builder.height,builder.getTheme());
        this.setChartTitle(builder.title);
        this.setXAxisTitle(builder.xAxisTitle);
        this.setYAxisTitle(builder.yAxisTitle);
        StyleManager sm = this.getStyleManager();
        sm.setChartType(builder.chartType);
    }
    
    public Series addSeries(String seriesName, Series series){
        super.addSeries(seriesName, series.getXData(), series.getYData());
        Map<String, Series> seriesMap = super.getSeriesMap();
        Series s = seriesMap.get(seriesName);
        All_series.put(seriesName, s);
        return s;
    }
    
    @Override
    public Series addSeries(String seriesName, double[] xData, double[] yData){
        super.addSeries(seriesName, xData, yData);
        Map<String, Series> seriesMap = super.getSeriesMap();
        Series series = seriesMap.get(seriesName);
        All_series.put(seriesName, series);
        return series;
    }
    
    @Override
    public Series addSeries(String seriesName, Collection<?> xData, Collection<? extends Number> yData) {
        super.addSeries(seriesName, xData, yData);
        Map<String, Series> seriesMap = super.getSeriesMap();
        Series series = seriesMap.get(seriesName);
        All_series.put(seriesName, series);
        return series;
    }
    
    /*
    * this series can not show again.
    */
    public void removeSeries(String seriesName){
        Map<String, Series> seriesMap = super.getSeriesMap();
        seriesMap.remove(seriesName);
        All_series.remove(seriesName);
    }
    
    public boolean showSeries(String seriesName){
        Series series = All_series.get(seriesName);
        if(series == null){
            System.out.println(seriesName +" not exists in All_series");
            return false;
        }
        Map<String, Series> seriesMap = super.getSeriesMap();
        if(seriesMap.get(seriesName) != null){
            System.out.println(seriesName +" alredy exists");
            return false;
        }
        seriesMap.put(seriesName, series);
        return true;
    }
    
    public void showAll(){
        Map<String, Series> seriesMap = super.getSeriesMap();
        seriesMap.clear();
        seriesMap.putAll(this.All_series);
    }
    
    public boolean showOnlyAverage(){
        Series series = All_series.get("Average");
        if(series == null){
            System.out.println("Average series do not exists in All_series");
            return false;
        }
        Map<String, Series> seriesMap = super.getSeriesMap();
        seriesMap.clear();
        seriesMap.put("Average", series);
        return true;
    }
    
    public boolean showWithoutAverage(){
        Series series = All_series.get("Average");
        if(series == null){
            System.out.println("Average series do not exists");
            return false;
        }
        Map<String, Series> seriesMap = super.getSeriesMap();
        seriesMap.clear();
        seriesMap.putAll(this.All_series);
        seriesMap.remove("Average");
        return true;
    }
    
    public void showAsModel(DefaultListModel<JCheckBox> model){
        Map<String, Series> seriesMap = super.getSeriesMap();
        seriesMap.clear();
        for(int i=0; i<model.size(); i++){
           JCheckBox c = (JCheckBox)model.getElementAt(i);
           if(c.isSelected()){
               String key = c.getText();
               Series value = this.All_series.get(key);
               seriesMap.put(key,value);
           }
        }
        if(this.All_series.get("Boundary") != null){
            seriesMap.put("Boundary", All_series.get("Boundary"));
        }
    }
    
    public int getCurrentSeriesNum(){
        return this.getSeriesMap().size();
    }
    
    public Map<String, Series> getAllSeriesMap() {
        return this.All_series;
    }
    
    /*
    just hide not delet this series, can invoke showSeries method to show again.
    */
    public boolean hideSeries(String seriesName){
        Map<String, Series> seriesMap = super.getSeriesMap();
        Series series = seriesMap.get(seriesName);
        if(series == null) return false;
        seriesMap.remove(seriesName);
        return true;
    }
}
