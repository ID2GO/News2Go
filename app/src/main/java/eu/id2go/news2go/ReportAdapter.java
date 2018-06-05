/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.id2go.news2go;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * An {@link ReportAdapter} knows how to create a list item layout for each report
 * in the data source (a list of {@link Report} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class ReportAdapter extends ArrayAdapter<Report> {

    /**
     * The part of the sectionTitle string from the USGS service that we use to determine
     * whether or not there is a sectionTitle offset present ("5km N of Cairo, Egypt").
     */
    private static final String LOCATION_SEPARATOR = " of ";

    /**
     * Constructs a new {@link ReportAdapter}.
     *
     * @param context of the app
     * @param reports is the list of reports, which is the data source of the adapter
     */
    public ReportAdapter(Context context, List<Report> reports) {
        super(context, 0, reports);
    }

    /**
     * Returns a list item view that displays information about the report at the given position
     * in the list of reports.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.report_list_item, parent, false);
        }

        // Find the report at the given position in the list of reports
        Report currentReport = getItem(position);
        /**
         * Binding the data of the report object to the views in the list_item layout based
         * on the view_id
         */
        // Find the TextView with view ID article
        TextView articleView = (TextView) listItemView.findViewById(R.id.article);
        // Format the articleTitle to show 1 decimal place
        articleView.setText(currentReport.getArticleTitle());


        // Get the original sectionTitle string from the Report object,
        // which can be in the format of "5km N of Cairo, Egypt" or "Pacific-Antarctic Ridge".
        String originalSectionTitle = currentReport.getSectionTitle();

        // If the original sectionTitle string (i.e. "5km N of Cairo, Egypt") contains
        // a primary sectionTitle (Cairo, Egypt) and a sectionTitle offset (5km N of that city)
        // then store the primary sectionTitle separately from the sectionTitle offset in 2 Strings,
        // so they can be displayed in 2 TextViews.
        String primarySectionTitle;
        String sectionTitleOffset;

        // Check whether the originalSectionTitle string contains the " of " text
        if (originalSectionTitle.contains(LOCATION_SEPARATOR)) {
            // Split the string into different parts (as an array of Strings)
            // based on the " of " text. We expect an array of 2 Strings, where
            // the first String will be "5km N" and the second String will be "Cairo, Egypt".
            String[] parts = originalSectionTitle.split(LOCATION_SEPARATOR);
            // SectionTitle offset should be "5km N " + " of " --> "5km N of"
            sectionTitleOffset = parts[0] + LOCATION_SEPARATOR;
            // Primary sectionTitle should be "Cairo, Egypt"
            primarySectionTitle = parts[1];
        } else {
            // Otherwise, there is no " of " text in the originalSectionTitle string.
            // Hence, set the default sectionTitle offset to say "Near the".
            sectionTitleOffset = getContext().getString(R.string.near_the);
            // The primary sectionTitle will be the full sectionTitle string "Pacific-Antarctic Ridge".
            primarySectionTitle = originalSectionTitle;
        }

        // Find the TextView with view ID sectionTitle
        TextView primarySectionTitleView = (TextView) listItemView.findViewById(R.id.primary_location);
        // Display the sectionTitle of the current report in that TextView
        primarySectionTitleView.setText(primarySectionTitle);

        // Find the TextView with view ID sectionTitle offset
        TextView sectionTitleOffsetView = (TextView) listItemView.findViewById(R.id
                .location_offset);
        // Display the sectionTitle offset of the current report in that TextView
        sectionTitleOffsetView.setText(sectionTitleOffset);

        // Create a new Date object from the time in milliseconds of the report
        Date dateObject = new Date(currentReport.getDateOfPublication());

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = formatDate(dateObject);
        // Display the date of the current report in that TextView
        dateView.setText(formattedDate);

        // Find the TextView with view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // Format the time string (i.e. "4:30PM")
        String formattedTime = formatTime(dateObject);
        // Display the time of the current report in that TextView
        timeView.setText(formattedTime);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }


    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL, dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a");
        return timeFormat.format(dateObject);
    }
}
