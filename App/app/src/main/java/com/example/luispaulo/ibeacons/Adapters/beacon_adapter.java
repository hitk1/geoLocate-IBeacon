package com.example.luispaulo.ibeacons.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.luispaulo.ibeacons.Model.Beacon;
import com.example.luispaulo.ibeacons.R;

import java.util.ArrayList;

/**
 * Created by Luis Paulo on 07/03/2018.
 */

public class beacon_adapter extends ArrayAdapter<Beacon> {

    private final Context context;
    private final ArrayList<Beacon> elementos;

    public beacon_adapter(Context context, ArrayList<Beacon> elementos)
    {
        super(context, R.layout.beacon_row, elementos);
        this.context = context;
        this.elementos = elementos;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.beacon_row, parent, false);

        TextView txtBeacon = (TextView)rowView.findViewById(R.id.txtBeacon);
        TextView txtKey = (TextView)rowView.findViewById(R.id.txtChave);

        txtBeacon.setText(elementos.get(position).getName());
        txtKey.setText(elementos.get(position).getKey());

        return rowView;
    }
}
