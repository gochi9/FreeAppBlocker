package gmail.developer_formal.freeappblocker.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.objects.AppInfoCache;

import java.util.*;

public class AppAdapter extends BaseAdapter implements Filterable {
    private final List<AppInfoCache> originalApps;
    private final List<AppInfoCache> filteredApps;
    private final Set<String> selectedApps;
    private final Set<String> originalSelectedApps;
    private final LayoutInflater inflater;
    private final Context context;

    public AppAdapter(Context context, Set<String> selectedApps) {
        List<AppInfoCache> apps = BlockersManager.getInstance(context).getAppsInfoCache();
        this.originalApps = new ArrayList<>(apps);
        this.filteredApps = new ArrayList<>(apps);
        this.selectedApps = new HashSet<>(selectedApps);
        this.originalSelectedApps = new HashSet<>(selectedApps);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        sortApps();
    }

    private void sortApps() {
        Map<ApplicationInfo, String> appLabels = new HashMap<>();
        for (AppInfoCache app : this.filteredApps)
            appLabels.put(app.getApplicationInfo(), app.getAppName().toString().toLowerCase());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Collections.sort(this.filteredApps, Comparator.comparing(app -> appLabels.get(app.getApplicationInfo())));
        else
            Collections.sort(this.filteredApps, (app1, app2) ->
                    appLabels.get(app1.getApplicationInfo()).compareTo(appLabels.get(app2.getApplicationInfo())));
    }

    @Override
    public int getCount() {
        return filteredApps.size();
    }

    @Override
    public AppInfoCache getItem(int position) {
        return filteredApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.app_list_item, parent, false);
            holder = new ViewHolder();
            holder.appName = convertView.findViewById(R.id.app_name);
            holder.appIcon = convertView.findViewById(R.id.app_icon);
            holder.appCheckbox = convertView.findViewById(R.id.app_checkbox);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        AppInfoCache cache = getItem(position);
        ApplicationInfo app = cache.getApplicationInfo();
        holder.appName.setText(cache.getAppName());
        holder.appIcon.setImageDrawable(cache.getAppIcon());
        holder.appCheckbox.setOnCheckedChangeListener(null);
        holder.appCheckbox.setChecked(selectedApps.contains(app.packageName));
        holder.appCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedApps.add(app.packageName);
                return;
            }

            if(!originalSelectedApps.contains(app.packageName) || !BlockersManager.getInstance(context).isStrictModeEnabled()){
                selectedApps.remove(app.packageName);
                return;
            }

            buttonView.setChecked(true);
            Toast.makeText(context, "Cannot do that while strict mode is enabled", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<AppInfoCache> filteredResults = new ArrayList<>();
                String filterPattern = (constraint == null) ? "" : constraint.toString().toLowerCase().trim();

                if (filterPattern.isEmpty())
                    filteredResults.addAll(originalApps);

                else if ("blocked".equals(filterPattern)) {
                    for (AppInfoCache app : originalApps)
                        if (selectedApps.contains(app.getApplicationInfo().packageName))
                            filteredResults.add(app);
                }
                else
                    for (AppInfoCache app : originalApps) {
                        String label = app.getAppName().toString().toLowerCase();
                        if (label.contains(filterPattern))
                            filteredResults.add(app);
                    }

                Collections.sort(filteredResults, (app1, app2) -> app1.getAppName().toString()
                        .compareToIgnoreCase(app2.getAppName().toString()));

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                results.count = filteredResults.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                synchronized (filteredApps) {
                    filteredApps.clear();
                    filteredApps.addAll((List<AppInfoCache>) results.values);
                    notifyDataSetChanged();
                }
            }
        };
    }


    public Set<String> getSelectedApps() {
        return selectedApps;
    }

    static class ViewHolder {
        TextView appName;
        ImageView appIcon;
        CheckBox appCheckbox;
    }
}
