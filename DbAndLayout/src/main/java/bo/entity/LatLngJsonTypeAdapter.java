package bo.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LatLngJsonTypeAdapter extends TypeAdapter<LatLng> {

    @Override
    public void write(JsonWriter out, LatLng value) throws IOException {
        // If you need to serialize LatLng back to JSON, implement this method
    }

    @Override
    public LatLng read(JsonReader in) throws IOException {
        double latitude = 0;
        double longitude = 0;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "Y": // Match this with your JSON's Y field
                    latitude = in.nextDouble();
                    break;
                case "X": // Match this with your JSON's X field
                    longitude = in.nextDouble();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return new LatLng(latitude, longitude);
    }
}