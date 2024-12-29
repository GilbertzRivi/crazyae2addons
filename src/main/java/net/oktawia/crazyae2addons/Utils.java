package net.oktawia.crazyae2addons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Utils {

    public static <T> List<T> rotate(List<T> inputList, int offset) {
        if (inputList.isEmpty()) {
            return new ArrayList<>(inputList); // Return empty list if input is empty
        }

        // Ensure the offset is within bounds of the list size
        int effectiveOffset = offset % inputList.size();
        if (effectiveOffset < 0) {
            effectiveOffset += inputList.size(); // Handle negative offsets
        }

        // Create a rotated list
        List<T> rotatedList = new ArrayList<>();
        rotatedList.addAll(inputList.subList(inputList.size() - effectiveOffset, inputList.size())); // End part
        rotatedList.addAll(inputList.subList(0, inputList.size() - effectiveOffset)); // Start part

        return rotatedList;
    }

    public static void asyncDelay(Runnable function, float delay) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        long delayInMillis = (long) (delay * 1000);
        scheduler.schedule(() -> {
            try {
                function.run();
            } finally {
                scheduler.shutdown();
            }
        }, delayInMillis, TimeUnit.MILLISECONDS);
    }
}