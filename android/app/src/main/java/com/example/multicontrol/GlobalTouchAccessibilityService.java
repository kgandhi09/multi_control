package com.example.multicontrol;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class GlobalTouchAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Check if the event is a touch interaction
        if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START ||
                event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {

            // You can get more details about the event if needed.
            int x = 90;
            int y = event.getY();

            // Send the touch event to BackgroundService
            Intent intent = new Intent(this, BackgroundService.class);
            intent.putExtra("eventType", event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START ? "MOUSE_DOWN" : "MOUSE_UP");
            intent.putExtra("x", x);
            intent.putExtra("y", y);
            startService(intent);

            Log.d("AccessibilityService", "Touch event: " + event.getEventType() + " at x:" + x + " y:" + y);
        }
    }

    @Override
    public void onInterrupt() {
        // Handle any interruptions to the service here
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_TOUCH_INTERACTION_START | AccessibilityEvent.TYPE_TOUCH_INTERACTION_END;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        setServiceInfo(info);
        Log.d("AccessibilityService", "Service connected");
    }
}
