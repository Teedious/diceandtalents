package no.domain.diceandtalents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import no.domain.diceandtalents.TalentStuff.Attributes;
import no.domain.diceandtalents.TalentStuff.Talent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class Talents_Act
        extends AppCompatActivity
        implements ListView.OnItemClickListener, ListView.OnItemLongClickListener
{
    private final String ST_FILENAME = "saved_talents";
    public static final String PREFS_NAME = "ListPrefs";
    private static final int NEW_TALENT_DIALOG = 0;
    private static final int EDIT_TALENT_DIALOG = 1;
    private ArrayList<Talent> data;
    private ListView talentList;
    private TalentListAdapter t;
    private TextView[] attrValTextViews;
    private TextView rollInfoView;
    private TextView resultView;
    private NumberPicker mod;
    MenuItem itemNewTal, itemEditAttr, itemTalentSettings;
    private Random rng;
    String TAG = "TalAct";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talents);
        rng = new Random();
        rng.setSeed(SystemClock.currentThreadTimeMillis());
        Toolbar tBar = findViewById(R.id.tal_toolbar);
        setSupportActionBar(tBar);

        attrValTextViews = new TextView[IDHolder.attrIDs.length];
        for(int i=0;i<IDHolder.attrIDs.length;i++){
            attrValTextViews[i] = findViewById(IDHolder.attrIDs[i]);
            attrValTextViews[i].setText(String.valueOf(Attributes.getValues()[i]));
        }

        restoreTalentData();
        t=new TalentListAdapter(this, R.layout.talent_list_row, data);
        talentList = findViewById(R.id.talentList);
        talentList.setAdapter(t);
        talentList.setOnItemClickListener(this);
        talentList.setOnItemLongClickListener(this);

        resultView = findViewById(R.id.talent_result_view);
        rollInfoView = findViewById(R.id.talent_roll_info_view);

        mod = findViewById(R.id.mod_picker);
        mod.setMinValue(0);
        mod.setMaxValue(40);
        mod.setValue(20);
        mod.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int i)
            {
                return String.valueOf(i-20);
            }
        });

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        saveTalentData();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.talent_menu, menu);


        itemNewTal = menu.findItem(R.id.menu_new_talent);
        itemEditAttr = menu.findItem(R.id.menu_edit_attr);
        itemTalentSettings = menu.findItem(R.id.menu_talent_settings);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_talent:
                Log.d(TAG, "new talent clicked!");
                createTalentDialog(null, NEW_TALENT_DIALOG);
                return true;

            case R.id.menu_edit_attr:
                Log.d(TAG, "edit attributes clicked!");
                createAttributeDialog();
                return true;

            case R.id.menu_talent_settings:
                Log.d(TAG, "talent settings clicked!");
                return true;


            default:
                Log.d(TAG, "something weird!");
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Talent t = ((TalentListAdapter.TalentViewHolder)view.getTag()).tal;
        t.roll(rng, mod.getValue()-20);
        mod.setValue(20);
        int[] diceResults = t.getLastDiceRoll();
        String rollInfo = t.getName()
                +" ("+t.getLastMod()+")"
                +": "+diceResults[0]
                + " · "+diceResults[1]
                + " · "+diceResults[2];
        String rollResult = (t.getLastTalentRollResult()>=0?"Bestanden ":"Nicht bestanden ")
                + "  ["+t.getLastTalentRollResult()+"]";
        rollInfoView.setText(rollInfo);
        resultView.setText(rollResult);
        if(t.getLastTalentRollResult()<0){
            resultView.setTextColor(Color.RED);
        }else resultView.setTextColor(Color.GREEN);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        return createTalentDialog(view, EDIT_TALENT_DIALOG);
    }

    private void createAttributeDialog(){
        final NumberPicker[] attrs = new NumberPicker[8];
        android.view.LayoutInflater inflater = (android.view.LayoutInflater) getSystemService(
                LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams")
        final View group = inflater.inflate(R.layout.attributes_pop_up, null, false);
        final int[] attrVals = Attributes.getValues();
        for(int i=0;i<attrs.length;i++){
            attrs[i]= group.findViewById(IDHolder.attrNPIDs[i]);
            attrs[i].setMaxValue(30);
            attrs[i].setMinValue(0);
            attrs[i].setValue(attrVals[i]);
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_edit_attributes)
                .setView(group)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                for(int i = 0; i< attrVals.length; i++){
                                    attrVals[i]=attrs[i].getValue();
                                    attrValTextViews[i].setText(String.valueOf(attrVals[i]));
                                }
                                if(!Attributes.setValues(attrVals))Log.e(TAG, "Attributes weren't set correctly");
                            }
                        })
                .setNegativeButton(android.R.string.cancel,null)
                .create().show();
    }

    private boolean createTalentDialog(final View view, int mode){

        final NumberPicker[] attrNP = new NumberPicker[3];
        android.view.LayoutInflater inflater = (android.view.LayoutInflater) getSystemService(
                LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams")
        final View group = inflater.inflate(R.layout.add_talent, null, false);
        final NumberPicker valueNP = group.findViewById(R.id.talentValPicker);
        valueNP.setMinValue(Talent.MIN_VAL);
        valueNP.setMaxValue(Talent.MAX_VAL);
        final EditText name = group.findViewById(R.id.edit_talent_name);
        for(int index = 0; index < attrNP.length; index++){
            attrNP[index] = group.findViewById(IDHolder.tAttrNPIDs[index]);
            attrNP[index].setDisplayedValues(Attributes.ATTR_NUM_STR.clone());
            attrNP[index].setMaxValue(7);
            attrNP[index].setMinValue(0);
        }
        if(mode == EDIT_TALENT_DIALOG)
        {
            final Talent talent = data.get(data.indexOf(((TalentListAdapter.TalentViewHolder) view.getTag()).tal));
            for (int index = 0; index < attrNP.length; index++)
            {
                attrNP[index].setValue(talent.getAttrID()[index]);
            }
            name.setText(talent.getName());
            valueNP.setValue(talent.getValue());

            new AlertDialog.Builder(this)
                    .setTitle(R.string.change_talent)
                    .setView(group)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    talent.setValue(valueNP.getValue());
                                    int[] attrIDs = new int[attrNP.length];
                                    for (int setAttrsIndex = 0; setAttrsIndex < attrIDs.length; setAttrsIndex++)
                                    {
                                        attrIDs[setAttrsIndex] = attrNP[setAttrsIndex].getValue();
                                    }
                                    talent.setAttrID(attrIDs);
                                    talent.setName(name.getText().toString());
                                    ((TalentListAdapter.TalentViewHolder) view.getTag()).tal = talent;
                                    t.notifyDataSetChanged();

                                }
                            })
                    .setNegativeButton(R.string.delete_talent, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            t.remove(talent);
                        }
                    })

                    .create().show();
        }else{
            final Talent talent = new Talent("", new int[]{0,0,0}, 0);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.menu_new_talent)
                    .setView(group)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    talent.setValue(valueNP.getValue());
                                    int[] attrIDs = new int[attrNP.length];
                                    for (int setAttrsIndex = 0; setAttrsIndex < attrIDs.length; setAttrsIndex++)
                                    {
                                        attrIDs[setAttrsIndex] = attrNP[setAttrsIndex].getValue();
                                    }
                                    talent.setAttrID(attrIDs);
                                    talent.setName(name.getText().toString());
                                    t.add(talent);
                                    t.notifyDataSetChanged();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,null)
                    .create().show();
        }
        return true;
    }

    void makeData(){
        data = new ArrayList<>(32);
        data.add(new Talent("Athletik", 5,2,3,0));
        data.add(new Talent("Klettern", 0,5,7,2));
        data.add(new Talent("Körperbeherrschung", 0,2,5,8));
        data.add(new Talent("Schleichen",0,2,5,7));
        data.add(new Talent("Schwimmen",5,6,7,5));
        data.add(new Talent("Selbsbeherrschung",0,6,7,3));
        data.add(new Talent("Sich Verstecken", 0,2,5,6));
        data.add(new Talent("Singen",2,3,3,4));
        data.add(new Talent("Sinnenschärfe",1,2,2,10));
        data.add(new Talent("Tanzen",3,5,5,4));
        data.add(new Talent("Zechen",2,6,7,0));
        data.add(new Talent("Menschenkenntnis", 1,2,3,9));
        data.add(new Talent("Überreden",0,2,3,10));
        data.add(new Talent("Betören",2,3,3,12));
        data.add(new Talent("Gassenwissen",1,2,3,5));
        data.add(new Talent("Etikette",1,2,3,6));
        data.add(new Talent("Sich Verkleiden",0,3,5,0));
        data.add(new Talent("Überzeugen",1,2,3,9));
        data.add(new Talent("Fährtensuchen", 1,2,2,2));
        data.add(new Talent("Orientierung",1,2,2,9));
        data.add(new Talent("Wildnisleben",2,5,6,7));
        data.add(new Talent("Fischen",2,4,7,2));
    }

    void saveTalentData(){
        try
        {
            FileOutputStream fileOutputStream = openFileOutput(ST_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(data);
            outputStream.close();
            fileOutputStream.close();
        }catch (IOException e){
            Log.e(TAG, e.getMessage(), e);
        }

    }

    void restoreTalentData(){
        try
        {
            FileInputStream fileInputStream = openFileInput(ST_FILENAME);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            data = (ArrayList<Talent>)inputStream.readObject();
        } catch (IOException e)
        {
            makeData();
            Log.e(TAG, e.getMessage(), e);
        } catch (ClassNotFoundException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
