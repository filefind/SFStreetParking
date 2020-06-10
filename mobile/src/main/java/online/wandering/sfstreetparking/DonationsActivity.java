package online.wandering.sfstreetparking;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DonationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations);
        TextView content = findViewById(R.id.content);
        String text = "Thx for your donations!\n\nWMZ\nZ382565296158\n\nBitcoin\n1M3fYjdLJzbVykdmMWCxek4t8w5KjBLFDU\n";
        content.setText(text);
    }
}
