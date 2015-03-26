
package hwdroid.preference;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.hw.droid.R;

public class HWSwitchPreference extends SwitchPreference {

    public HWSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HWSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HWSwitchPreference(Context context) {
        super(context);
    }

    private final Listener mListener = new Listener();

    private class Listener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!callChangeListener(isChecked)) {
                buttonView.setChecked(!isChecked);
                return;
            }

            HWSwitchPreference.this.setChecked(isChecked);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        final View layout = layoutInflater.inflate(R.layout.hw_common_preference, parent, false);

        final ViewGroup widgetFrame = (ViewGroup) layout.findViewById(android.R.id.widget_frame);
        if (widgetFrame != null) {
            layoutInflater.inflate(R.layout.hw_common_preference_widget_switch, widgetFrame);
        }

        this.setSwitchTextOn("");
        this.setSwitchTextOff("");

        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        View checkableView = view.findViewById(R.id.switchWidget);
        if (checkableView != null && checkableView instanceof Checkable) {
            ((Checkable) checkableView).setChecked(isChecked());

            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;

                switchView.setOnCheckedChangeListener(mListener);
            }
        }
    }
}
