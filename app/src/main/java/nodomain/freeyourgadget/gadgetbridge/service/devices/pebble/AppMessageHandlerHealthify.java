package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventSendBytes;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

class AppMessageHandlerHealthify extends AppMessageHandler {
    private static final int KEY_TEMPERATURE = 10021;
    private static final int KEY_CONDITIONS = 10022;

    AppMessageHandlerHealthify(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    private byte[] encodeMarioWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }

        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        pairs.add(new Pair<>(KEY_CONDITIONS, (Object) weatherSpec.currentCondition));
        pairs.add(new Pair<>(KEY_TEMPERATURE, (Object) (weatherSpec.currentTemp - 273)));
        byte[] weatherMessage = mPebbleProtocol.encodeApplicationMessagePush(PebbleProtocol.ENDPOINT_APPLICATIONMESSAGE, mUUID, pairs);

        ByteBuffer buf = ByteBuffer.allocate(weatherMessage.length);

        buf.put(weatherMessage);

        return buf.array();
    }

    @Override
    public GBDeviceEvent[] onAppStart() {
        WeatherSpec weatherSpec = Weather.getInstance().getWeatherSpec();
        if (weatherSpec == null) {
            return new GBDeviceEvent[]{null};
        }
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodeMarioWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    @Override
    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeMarioWeatherMessage(weatherSpec);
    }
}
