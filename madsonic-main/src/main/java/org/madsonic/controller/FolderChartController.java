/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package org.madsonic.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.springframework.web.servlet.ModelAndView;

import org.madsonic.dao.MusicFolderDao;
import org.madsonic.dao.MusicFolderStatisticsDao;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.MusicFolderStatistics;
import org.madsonic.domain.User;
import org.madsonic.service.SecurityService;

/**
 * Controller for generating a chart showing bitrate vs time.
 *
 * @author Sindre Mehus
 */
public class FolderChartController extends AbstractChartController {


    private MusicFolderDao musicFolderDao;
    private MusicFolderStatisticsDao musicFolderStatisticsDao;
   
    
    public static final int IMAGE_WIDTH = 820;
    public static final int IMAGE_MIN_HEIGHT = 35;
    
    private static final long BYTES_PER_MB = 1024L * 1024L;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String type = request.getParameter("type");
        CategoryDataset dataset = createDataset(type);
        JFreeChart chart = createChart(dataset, request);
        
        int imageHeight = IMAGE_MIN_HEIGHT * dataset.getColumnCount() ;
        if (imageHeight < 100){
        	imageHeight = 100;
        }
        
        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, IMAGE_WIDTH, imageHeight);
        return null;
    }

    private CategoryDataset createDataset(String type) {
    	
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<Integer, String> mapFolder = new HashMap<Integer, String>();
        
        List <MusicFolder> allMusicFolders = musicFolderDao.getAllEnabledMusicFolders();
        
        for (MusicFolder musicFolders : allMusicFolders) {
        	mapFolder.put(musicFolders.getId(), musicFolders.getName());
        }
        List<MusicFolderStatistics> statList = musicFolderStatisticsDao.getAllFolderStatistics();
        for (MusicFolderStatistics _statList : statList) {
            double value;
            
            MusicFolder x = musicFolderDao.getMusicFolder(_statList.getMusicFolderId());

            // show only enabled folder
            if (x.isEnabled()) {
            
            if ("song".equals(type)) {
                value = _statList.getSongCount();
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));

            } else if ("songSize".equals(type)) {
                value = _statList.getSongSize();
                value /= BYTES_PER_MB;
                value = Math.round(value);
                value = Double.valueOf(value);                
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else if ("video".equals(type)) {
                value = _statList.getVideoCount();
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else if ("videoSize".equals(type)) {
                value = _statList.getVideoSize();
                value /= BYTES_PER_MB;
                value = Math.round(value);
                value = Double.valueOf(value);                
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else if ("album".equals(type)) {
                value = _statList.getAlbumCount();
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else if ("podcast".equals(type)) {
                value = _statList.getPodcastCount();
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else if ("podcastSize".equals(type)) {
                value = _statList.getPodcastSize();
                value /= BYTES_PER_MB;
                value = Math.round(value);
                value = Double.valueOf(value);                
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else if ("audiobook".equals(type)) {
                value = _statList.getAudiobookCount();
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else if ("audiobookSize".equals(type)) {
                value = _statList.getAudiobookSize();
                value /= BYTES_PER_MB;
                value = Math.round(value);
                value = Double.valueOf(value);                
                dataset.addValue(value, "Series", mapFolder.get(_statList.getMusicFolderId()));
            } else {
                throw new RuntimeException("Illegal chart type: " + type);
            }
        }
            
        }

        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset, HttpServletRequest request) {
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
        CategoryPlot plot = chart.getCategoryPlot();
        Paint background = new GradientPaint(0, 0, Color.lightGray, 0, IMAGE_MIN_HEIGHT, Color.white);
        plot.setBackgroundPaint(background);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.gray);
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

        LogarithmicAxis rangeAxis = new LogarithmicAxis(null);
        rangeAxis.setStrictValuesFlag(false);
        rangeAxis.setAllowNegativesFlag(true);
//        rangeAxis.setTickUnit(new NumberTickUnit(.1, new DecimalFormat("##0%")));
        plot.setRangeAxis(rangeAxis);

        // Disable bar outlines.
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // Set up gradient paint for series.
        GradientPaint gp0 = new GradientPaint(
                0.0f, 0.0f, Color.gray,
                0.0f, 0.0f, new Color(0, 0, 64)
        );
        renderer.setSeriesPaint(0, gp0);

        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelFont(new Font("SansSerif", Font.BOLD, 11));
        renderer.setItemLabelAnchorOffset(-45.0);
        
        renderer.setSeriesItemLabelPaint(0,Color.white);
        renderer.setBaseItemLabelPaint(Color.white);

        // Rotate labels.
        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        // Set theme-specific colors.
        Color bgColor = getBackground(request); 
        Color fgColor = getForeground(request);

        chart.setBackgroundPaint(bgColor);

        domainAxis.setTickLabelPaint(fgColor);
        domainAxis.setTickMarkPaint(fgColor);
        domainAxis.setAxisLinePaint(fgColor);

        rangeAxis.setTickLabelPaint(fgColor);
        rangeAxis.setTickMarkPaint(fgColor);
        rangeAxis.setAxisLinePaint(fgColor);

        return chart;
    }

    public void setMusicFolderStatisticsDao(MusicFolderStatisticsDao musicFolderStatisticsDao) {
        this.musicFolderStatisticsDao = musicFolderStatisticsDao;
    }

    public void setMusicFolderDao(MusicFolderDao musicFolderDao) {
        this.musicFolderDao = musicFolderDao;
    }


}
