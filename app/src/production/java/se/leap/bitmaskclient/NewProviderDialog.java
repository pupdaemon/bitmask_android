/**
 * Copyright (c) 2013 LEAP Encryption Access Project and contributers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package se.leap.bitmaskclient;

import butterknife.*;
import se.leap.bitmaskclient.ProviderListContent.ProviderItem;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

/**
 * Implements the new custom provider dialog.
 *
 * @author parmegv
 */
public class NewProviderDialog extends DialogFragment {

    final public static String TAG = "newProviderDialog";

    @InjectView(R.id.new_provider_url)
    EditText url_input_field;

    public interface NewProviderDialogInterface {
        public void showAndSelectProvider(String url_provider);
    }

    NewProviderDialogInterface interface_with_ConfigurationWizard;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            interface_with_ConfigurationWizard = (NewProviderDialogInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_provider_dialog, null);
        ButterKnife.inject(this, view);
        Bundle arguments = getArguments();
        if (arguments != null) {
            url_input_field.setText(arguments.getString(Provider.MAIN_URL, ""));
        }

        builder.setView(view)
                .setMessage(R.string.introduce_new_provider)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveProvider();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void saveProvider() {
        String entered_url = url_input_field.getText().toString().trim();
        if (!entered_url.startsWith("https://")) {
            if (entered_url.startsWith("http://")) {
                entered_url = entered_url.substring("http://".length());
            }
            entered_url = "https://".concat(entered_url);
        }

        if (validURL(entered_url)) {
            interface_with_ConfigurationWizard.showAndSelectProvider(entered_url);
            Toast.makeText(getActivity().getApplicationContext(), R.string.valid_url_entered, Toast.LENGTH_LONG).show();
        } else {
            url_input_field.setText("");
            Toast.makeText(getActivity().getApplicationContext(), R.string.not_valid_url_entered, Toast.LENGTH_LONG).show();
            ;
        }
    }

    /**
     * Checks if the entered url is valid or not.
     *
     * @param entered_url
     * @return true if it's not empty nor contains only the protocol.
     */
    boolean validURL(String entered_url) {
        //return !entered_url.isEmpty() && entered_url.matches("http[s]?://.+") && !entered_url.replaceFirst("http[s]?://", "").isEmpty();
        return android.util.Patterns.WEB_URL.matcher(entered_url).matches();
    }
}
