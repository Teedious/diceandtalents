package no.domain.diceandtalents;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import no.domain.diceandtalents.TalentStuff.Attributes;
import no.domain.diceandtalents.TalentStuff.Talent;

public class TalentListAdapter extends ArrayAdapter<Talent>
{
    int layout;
    public TalentListAdapter(@NonNull Context context, int resource, @NonNull List<Talent> objects)
    {
        super(context, resource, objects);
        layout = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        TalentViewHolder holder=null;
        if(convertView==null)
        {
            LayoutInflater talentViewInflater = LayoutInflater.from(getContext());
            convertView = talentViewInflater.inflate(
                    R.layout.talent_list_row,
                    parent,
                    false);

            final Talent tal = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.row_talent_name);
            TextView val = (TextView) convertView.findViewById(R.id.row_val);
            TextView attr = (TextView) convertView.findViewById(R.id.row_attr);

            name.setText(tal.getName());
            int[] ids = tal.getAttrID();
            attr.setText(
                    Attributes.ATTR_NUM_STR[ids[0]]
                            +" · "+Attributes.ATTR_NUM_STR[ids[1]]
                            +" · "+Attributes.ATTR_NUM_STR[ids[2]]
            );
            val.setText(String.valueOf(tal.getValue()));

            holder = new TalentViewHolder(tal, name, attr, val);
            convertView.setTag(holder);
        }
        else
        {
            holder = (TalentViewHolder) convertView.getTag();
            holder.tal=getItem(position);
            holder.name.setText(holder.tal.getName());
            holder.value.setText(String.valueOf(holder.tal.getValue()));
            int[] ids = holder.tal.getAttrID();
            holder.attr.setText(
                    Attributes.ATTR_NUM_STR[ids[0]]
                    +" · "+Attributes.ATTR_NUM_STR[ids[1]]
                    +" · "+Attributes.ATTR_NUM_STR[ids[2]]
            );
        }
        return convertView;
    }

    public class TalentViewHolder{
        Talent tal;
        TextView name;
        TextView attr;
        TextView value;

        public TalentViewHolder(Talent talent,
                                TextView name,
                                TextView attr,
                                TextView val)
        {
            tal = talent;
            this.name=name;
            this.attr=attr;
            value = val;
        }
    }
}
