package angelcotes.firebase_final;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private Firebase myFirebaseRef;
    private FloatingActionButton btn_add;
    private String url = "flickering-inferno-8255";
    private int index = 0;
    private ListView lista;
    private DataSnapshot informations;
    private Vector<String> planets =  new Vector<String>();
    private ArrayAdapter<String> adapter;
    private Context information;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        myFirebaseRef = new Firebase("https://" + url +".firebaseio.com/");
        btn_add = (FloatingActionButton) findViewById(R.id.fab);
        lista = (ListView)findViewById(R.id.lista);
        information = this;

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                planets.clear();
                if(adapter != null)
                    adapter.clear();
                lista.setAdapter(null);
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if (index == 0)
                    index = (int)dataSnapshot.getChildrenCount();
                while (iterator.hasNext()) {
                    planets.add(iterator.next().child("name").getValue(String.class));
                }
                adapter = new ArrayAdapter<String>(information, R.layout.list_content, R.id.textView, planets);
                lista.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("name", "JAC "+ (index));
                myFirebaseRef.push().setValue(item);
                index = index + 1;
                planets.clear();
                adapter.clear();
                lista.clearTextFilter();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    url = input.getText().toString();
                    myFirebaseRef = new Firebase("https://" + url + ".firebaseio.com/");
                    myFirebaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            planets.clear();
                            if(adapter != null)
                                adapter.clear();
                            lista.setAdapter(null);
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            index = (int)dataSnapshot.getChildrenCount();
                            while (iterator.hasNext()) {
                                planets.add(iterator.next().child("name").getValue(String.class));
                            }
                            adapter = new ArrayAdapter<String>(information, R.layout.list_content, R.id.textView, planets);
                            lista.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
