package com.tache.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tache.R;
import com.tache.dialogs.MyDatePickerDialog;
import com.tache.dialogs.UploadingDialog;
import com.tache.events.ProfileUpdatedEvent;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.profile.ProfileInfo;
import com.tache.rest.models.request.UpdateUser;
import com.tache.rest.models.response.User;
import com.tache.rest.services.ProfileService;
import com.tache.rest.services.UserService;
import com.tache.utils.Helper;
import com.tache.utils.ImageUploadHelper;
import com.tache.utils.SharedPrefsUtils;
import com.tache.utils.TimeFormatHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mayank on 13/10/16.
 */

@RuntimePermissions
public class EditProfileFragment extends Fragment {

    public static final String ARG_PROFILE_INFO = EditProfileFragment.class.getPackage().getName() + ".profile_info";
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @BindView(R.id.frag_edit_profile_circle_user_image)
    CircularImageView circleUserImage;
    @BindView(R.id.frag_edit_profile_indicator_2)
    View indicator2;
    @BindView(R.id.frag_edit_profile_title)
    TextView title;
    @BindView(R.id.frag_personal_info_gender)
    Spinner gender;
    @BindView(R.id.frag_personal_info_dob)
    TextView dob;
    @BindView(R.id.frag_personal_info_pin_code)
    EditText pinCode;
    @BindView(R.id.frag_edit_profile_personal_info_table)
    View personalInfoTable;
    @BindView(R.id.frag_professional_qualification)
    Spinner qualification;
    @BindView(R.id.frag_professional_employment)
    Spinner employment;
    @BindView(R.id.frag_professional_designation)
    Spinner designation;
    @BindView(R.id.frag_professional_designation_other)
    EditText designationOther;
    @BindView(R.id.frag_professional_industry)
    Spinner industry;
    @BindView(R.id.frag_professional_work_experience)
    Spinner workExperience;
    @BindView(R.id.frag_professional_annual_income)
    Spinner annualIncome;
    @BindView(R.id.frag_edit_profile_professional_info_table)
    View professionalInfoTable;
    @BindView(R.id.frag_edit_profile_whats_this)
    TextView whatsThis;
    @BindView(R.id.frag_edit_profile_next)
    TextView next;

//    PlaceAutocompleteFragment autocompleteFragment;
//    EditText autocompleteFragmentEditText;

    private ArrayList<String> filePaths;
    private UploadingDialog progressDialog;
    private boolean isTab2;
    private SharedPrefsUtils sharedPrefsUtils;
    private Unbinder unbinder;
    private ProfileInfo profileInfo;
    private boolean isInEditMode;
    //    private GoogleApiClient googleApiClient;
//    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private LatLng areaLatLng;
    private int to = -1;

    public static EditProfileFragment newInstance(@Nullable ProfileInfo profileInfo) {
        EventBus.getDefault().removeAllStickyEvents();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROFILE_INFO, profileInfo);
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

//        autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragmentEditText = ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
//        autocompleteFragmentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//        autocompleteFragment.setHint("Area");
//
//        int padding = Helper.pxToDp(getContext(), 8);
//        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
//        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setPadding(padding, padding, padding, padding);

        areaLatLng = new LatLng(28.6820895, 77.292985);

        sharedPrefsUtils = new SharedPrefsUtils(getContext());

        profileInfo = getArguments().getParcelable(ARG_PROFILE_INFO);
        isInEditMode = profileInfo != null;

        designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((designation.getCount() - 1) == position) {
                    designationOther.setVisibility(View.VISIBLE);
                } else {
                    designationOther.setText("");
                    designationOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isInEditMode)
            setData();
        else
            setUserImage(null);

//        googleApiClient = new GoogleApiClient.Builder(getContext())
//                .addApi(Places.GEO_DATA_API)
//                .build();
//        googleApiClient.connect();
//        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), googleApiClient, BOUNDS_GREATER_SYDNEY, null);

//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                autocompleteFragment.setText(place.getName().toString());
//                areaLatLng = place.getLatLng();
//            }
//
//            @Override
//            public void onError(Status status) {
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (to != -1 && to == 1) {
            if (!isTab2) {
                if (arePersonalDetailsValid()) {
                    indicator2.setBackgroundResource(R.color.colorAccent);
                    title.setText("Professional Details");
                    whatsThis.setText("Previous");
                    whatsThis.setVisibility(View.VISIBLE);
                    next.setText("Finish");
                    professionalInfoTable.setVisibility(View.VISIBLE);
                    personalInfoTable.setVisibility(View.GONE);
                    isTab2 = true;
                }
            }
        }
    }

    private void setUserImage(@Nullable String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(getContext())
                    .load(imageUrl).placeholder(ContextCompat.getDrawable(getContext(), R.drawable.profile_inactive))
                    .dontAnimate()
                    .into(circleUserImage);
        } else {
            circleUserImage.setImageResource(R.drawable.profile_inactive);
        }
    }

    private void setData() {
        setUserImage(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_IMAGE));
        gender.setSelection(profileInfo.getGender().equals("f") ? 1 : 0);
        dob.setText(TimeFormatHelper.getDateInAppropriateFormat(profileInfo.getDob()));
        pinCode.setText(profileInfo.getPincode());
        //autocompleteFragment.setText(profileInfo.getArea());
        qualification.setSelection(getIndex(qualification, profileInfo.getQualification()));
        employment.setSelection(getIndex(employment, profileInfo.getEmployment()));
        designation.setSelection(getIndex(designation, profileInfo.getDesignation()));
        designationOther.setText(profileInfo.getDesignation_other());
        industry.setSelection(getIndex(industry, profileInfo.getIndustry()));
        workExperience.setSelection(getIndex(workExperience, profileInfo.getWorkExperience()));
        annualIncome.setSelection(getIndex(annualIncome, profileInfo.getAnnualIncome()));
        areaLatLng = new LatLng(profileInfo.getAreaLat(), profileInfo.getAreaLong());
    }

    private int getIndex(Spinner spinner, String string) {
        int index = 0, count = spinner.getCount();
        for (int i = 0; i < count; i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void moveTo(int to) {
        this.to = to;
    }

    @OnClick({R.id.frag_personal_info_dob, R.id.frag_edit_profile_select_image, R.id.frag_edit_profile_whats_this, R.id.frag_edit_profile_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frag_personal_info_dob:
                final Calendar calendar = Calendar.getInstance();
                MyDatePickerDialog myDatePickerDialog = new MyDatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, day);
                        Date date = new Date();
                        date.setTime(selectedDate.getTimeInMillis());
                        dob.setText(TimeFormatHelper.getDateInAppropriateFormat(date));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.setPermanentTitle("Date of Birth");
                myDatePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                myDatePickerDialog.show();
                break;
            case R.id.frag_edit_profile_select_image:
                EditProfileFragmentPermissionsDispatcher.selectPicWithCheck(EditProfileFragment.this);
                break;
            case R.id.frag_edit_profile_whats_this:
                if (isTab2) {
                    indicator2.setBackgroundResource(R.color.colorDivider);
                    title.setText("Personal Details");
                    whatsThis.setVisibility(View.GONE);
                    next.setText("Next");
                    professionalInfoTable.setVisibility(View.GONE);
                    personalInfoTable.setVisibility(View.VISIBLE);
                    isTab2 = false;
                } else {
                    Toast.makeText(getContext(), "What's this", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.frag_edit_profile_next:
                if (!isTab2) {
                    if (arePersonalDetailsValid()) {
                        indicator2.setBackgroundResource(R.color.colorAccent);
                        title.setText("Professional Details");
                        whatsThis.setText("Previous");
                        whatsThis.setVisibility(View.VISIBLE);
                        next.setText("Finish");
                        professionalInfoTable.setVisibility(View.VISIBLE);
                        personalInfoTable.setVisibility(View.GONE);
                        isTab2 = true;
                    }
                } else {
                    boolean isImageChanged = isImageChanged();
                    boolean areDetailsChanged = areDetailsChanged();
                    if (!isImageChanged && !areDetailsChanged) {
                        Toast.makeText(getContext(), "No changes are required", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new ProfileUpdatedEvent(true));
                    } else
                        updateDetails(isImageChanged, areDetailsChanged);
                }
                break;
        }
    }

    private void updateDetails(boolean isImageChanged, final boolean areDetailsChanged) {
        if (isImageChanged) {
            progressDialog = new UploadingDialog();
            progressDialog.setCancelable(false);
            progressDialog.show(getChildFragmentManager(), progressDialog.getTag() + System.nanoTime());

            new ImageUploadHelper(getContext(), 200).uploadImagesOnS3(filePaths, new ImageUploadHelper.ImageUploadListener() {
                @Override
                public void onUploadCompleted(List<String> imageUrls) {
                    if (imageUrls != null && imageUrls.size() > 0) {
                        setUserImage(imageUrls.get(0));
                        sharedPrefsUtils.setStringPreference(SharedPrefsUtils.USER_IMAGE, imageUrls.get(0));
                        updateImageUrl(imageUrls.get(0));
                        progressDialog.stopOk(true);
                    }
                    if (areDetailsChanged)
                        userDetailsUpdateCall();
                    else
                        EventBus.getDefault().post(new ProfileUpdatedEvent(true));
                }

                @Override
                public void onError() {
                    progressDialog.stopOk(false);
                    if (areDetailsChanged)
                        userDetailsUpdateCall();
                    else
                        EventBus.getDefault().post(new ProfileUpdatedEvent(true));
                }
            });
        } else if (areDetailsChanged)
            userDetailsUpdateCall();
    }

    private boolean isImageChanged() {
        return (filePaths != null && filePaths.size() > 0);
    }

    private boolean areDetailsChanged() {
        return !isInEditMode
                || !(profileInfo.getGender().equals("f") ? "Female" : "Male").equalsIgnoreCase(gender.getSelectedItem().toString())
                || !profileInfo.getDob().equalsIgnoreCase(TimeFormatHelper.getDateInStandardFormat(dob.getText().toString()))
                || !profileInfo.getPincode().equalsIgnoreCase(pinCode.getText().toString())
                //|| !profileInfo.getArea().equalsIgnoreCase(((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString())
                || !profileInfo.getQualification().equalsIgnoreCase(qualification.getSelectedItem().toString())
                || !profileInfo.getEmployment().equalsIgnoreCase(employment.getSelectedItem().toString())
                || !profileInfo.getDesignation().equalsIgnoreCase(designation.getSelectedItem().toString())
                || !(profileInfo.getDesignation_other() == null ? "" : profileInfo.getDesignation_other()).equalsIgnoreCase(designationOther.getText().toString())
                || !profileInfo.getIndustry().equalsIgnoreCase(industry.getSelectedItem().toString())
                || !profileInfo.getWorkExperience().equalsIgnoreCase(workExperience.getSelectedItem().toString())
                || !profileInfo.getAnnualIncome().equalsIgnoreCase(annualIncome.getSelectedItem().toString());
    }

    private boolean arePersonalDetailsValid() {
        if (TextUtils.isEmpty(dob.getText())) {
            showToast("Please enter your Date of Birth");
            return false;
        }
        if (TextUtils.isEmpty(pinCode.getText())) {
            showToast("Please enter your area pincode");
            return false;
        }
//        if (TextUtils.isEmpty(((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString())) {
//            showToast("Please enter your area");
//            return false;
//        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void userDetailsUpdateCall() {
        ProfileInfo profileInfo = isInEditMode ? this.profileInfo : new ProfileInfo();
        boolean valid = getProfileInfo(profileInfo);

        if (valid) {
            ProfileService profileService = ApiUtils.retrofitInstance().create(ProfileService.class);
            Call<ProfileInfo> updateProfileCall = isInEditMode
                    ? profileService.updateProfile(Helper.getAuthHeader(getContext()), profileInfo)
                    : profileService.createProfile(Helper.getAuthHeader(getContext()), profileInfo);
            updateProfileCall.enqueue(new Callback<ProfileInfo>() {
                @Override
                public void onResponse(Call<ProfileInfo> call, Response<ProfileInfo> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "User details updated", Toast.LENGTH_SHORT).show();
                        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_HAS_PROFILE, true);
                        EventBus.getDefault().post(new ProfileUpdatedEvent(true));
                    } else {
                        Toast.makeText(getContext(), "Cannot update user details at the moment", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProfileInfo> call, Throwable t) {
                    Toast.makeText(getContext(), "Cannot update user details at the moment", Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Toast.makeText(getContext(), "No fields can be left empty.", Toast.LENGTH_SHORT).show();

    }

    private void updateImageUrl(String imageUrl) {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setImage_url(imageUrl);
        updateUser.setIs_mobile_verified(true);
        updateUser.setName(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_NAME));
        updateUser.setMobile(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_MOBILE));

        UserService userService = ApiUtils.retrofitInstance().create(UserService.class);
        userService.updateUser(Helper.getAuthHeader(getContext()), updateUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private boolean getProfileInfo(ProfileInfo profileInfo) {
        profileInfo.setGender(gender.getSelectedItem().toString().equalsIgnoreCase("Male") ? "m" : "f");
        profileInfo.setDob(TimeFormatHelper.getDateInStandardFormat(dob.getText().toString()));
        profileInfo.setPincode(pinCode.getText().toString());
        //profileInfo.setArea(((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString());
        profileInfo.setArea("Delhi");
        profileInfo.setAreaLat(areaLatLng.latitude);
        profileInfo.setAreaLong(areaLatLng.longitude);
        profileInfo.setQualification(qualification.getSelectedItem().toString());
        profileInfo.setEmployment(employment.getSelectedItem().toString());
        profileInfo.setDesignation(designation.getSelectedItem().toString());
        profileInfo.setDesignation_other(designationOther.getText().toString());
        profileInfo.setIndustry(industry.getSelectedItem().toString());
        profileInfo.setWorkExperience(workExperience.getSelectedItem().toString());
        profileInfo.setAnnualIncome(annualIncome.getSelectedItem().toString());

        boolean toReturn = designationOther.getVisibility() == View.GONE || !TextUtils.isEmpty(designationOther.getText());

        if (qualification.getSelectedItemPosition() == 0)
            showSpinnerError(qualification, "Qualification required");
        if (employment.getSelectedItemPosition() == 0)
            showSpinnerError(employment, "Detail required");
        if (industry.getSelectedItemPosition() == 0)
            showSpinnerError(industry, "Industry required");
        if (annualIncome.getSelectedItemPosition() == 0)
            showSpinnerError(annualIncome, "Annual income required");

        if (qualification.getSelectedItemPosition() == 0 || employment.getSelectedItemPosition() == 0 || industry.getSelectedItemPosition() == 0 || annualIncome.getSelectedItemPosition() == 0)
            toReturn = false;

        return toReturn;
    }

    private void showSpinnerError(Spinner spinner, String error) {
        ((TextView) spinner.getSelectedView()).setText(error);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void selectPic() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths != null ? filePaths : new ArrayList<String>())
                .setActivityTheme(R.style.AppTheme)
                .pickPhoto(EditProfileFragment.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        EditProfileFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleForCamera(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_read_ext_storage_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showDeniedForCamera() {
        Toast.makeText(getContext(), R.string.permission_read_ext_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showNeverAskForCamera() {
        Toast.makeText(getContext(), R.string.permission_read_ext_storage_never_askagain, Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    filePaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    if (filePaths != null) {
                        if (filePaths.size() > 0) {
                            setUserImage(filePaths.get(0));
                        } else {
                            setUserImage(null);
                        }
                    }
                }
        }
    }

//    private AdapterView.OnItemClickListener autocompleteClickListener
//            = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            /*
//             Retrieve the place ID of the selected item from the Adapter.
//             The adapter stores each Place suggestion in a AutocompletePrediction from which we
//             read the place ID and title.
//              */
//            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
//            final String placeId = item.getPlaceId();
//            final CharSequence primaryText = item.getPrimaryText(null);
//
//            Timber.d("Autocomplete item selected: " + primaryText);
//
//            /*
//             Issue a request to the Places Geo Data API to retrieve a Place object with additional
//             details about the place.
//              */
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(googleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//
//            Toast.makeText(getContext().getApplicationContext(), "Clicked: " + primaryText,
//                    Toast.LENGTH_SHORT).show();
//            Timber.d("Called getPlaceById to get Place details for " + placeId);
//        }
//    };

//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                // Request did not complete successfully
//                Timber.d("Place query did not complete. Error: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            areaLatLng = places.get(0).getLatLng();
//            places.release();
//        }
//    };

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//        Timber.d("onConnectionFailed: ConnectionResult.getErrorCode() = "
//                + connectionResult.getErrorCode());
//
//        Toast.makeText(getContext(),
//                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
//                Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (googleApiClient != null) {
//            googleApiClient.disconnect();
//        }
        //removePlaceAutocomplete();
        unbinder.unbind();
    }

//    private void removePlaceAutocomplete() {
//        android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
//        android.app.Fragment fragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment);
//        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.remove(fragment);
//        fragmentTransaction.commit();
//    }

}
