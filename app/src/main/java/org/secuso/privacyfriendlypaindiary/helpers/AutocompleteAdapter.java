package org.secuso.privacyfriendlypaindiary.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Example found at: <a href="https://stackoverflow.com/questions/33047156/how-to-create-custom-baseadapter-for-autocompletetextview"/a>.
 *
 * @author Susanne Felsen
 * @version 20180105
 */
public class AutocompleteAdapter extends ArrayAdapter<DrugInterface> {

    private static final String TAG = AutocompleteAdapter.class.getSimpleName();

    private int mResourceId;
    private List<DrugInterface> originalList;
    private List<DrugInterface> suggestions;
    private boolean itemClicked = false;
    private String dose;
    private Filter filter = new CustomFilter();

    public AutocompleteAdapter(Context context, int textViewResourceId, List<DrugInterface> originalList) {
        super(context, textViewResourceId, originalList);
        this.mResourceId = textViewResourceId;
        this.originalList = originalList;
        this.suggestions = new ArrayList<>(originalList);
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public DrugInterface getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final View view;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(mResourceId, parent, false);
        } else {
            view = convertView;
        }

        final DrugInterface drug = getItem(position);
        if(drug != null) {
            String nameToDisplay = drug.getName();
            if(drug.getDose() != null) {
                nameToDisplay += " (" + drug.getDose() + ")";
            }

            if(view instanceof TextView) {
                ((TextView) view).setText(nameToDisplay);
            }
        }

        return view;

    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public boolean isItemClicked() {
        return itemClicked;
    }

    public void setItemClicked(boolean selected) {
        itemClicked = selected;
    }

    public String getDose() {
        return dose;
    }

    private class CustomFilter extends Filter {

        @Override
        public String convertResultToString(Object resultValue) {
            DrugInterface drug = (DrugInterface) resultValue;
            dose = drug.getDose();
            return drug.getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<DrugInterface> tempSuggestions = new ArrayList<>();
            if (constraint != null) {
                for (DrugInterface drug : originalList) {
                    if (drug.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        tempSuggestions.add(drug);
                    }
                }
                filterResults.values = tempSuggestions;
                filterResults.count = tempSuggestions.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            suggestions.clear();
            if (results != null && results.count > 0) {
                // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                for (Object object : (List<?>) results.values) {
                    if (object instanceof DrugInterface) {
                        suggestions.add((DrugInterface) object);
                    }
                }
                notifyDataSetChanged();
            } else if (constraint == null) {
                // no filter, add entire original list back in
                suggestions.addAll(originalList);
                notifyDataSetInvalidated();
            }
        }

    }

}
