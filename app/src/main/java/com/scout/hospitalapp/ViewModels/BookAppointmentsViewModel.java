package com.scout.hospitalapp.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.scout.hospitalapp.Activities.BookAppointmentActivity;
import com.scout.hospitalapp.Models.ModelBookAppointment;
import com.scout.hospitalapp.Models.ModelDateTime;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelUnAvailableDates;
import com.scout.hospitalapp.Repository.Remote.AppointmentsRepo;
import com.scout.hospitalapp.Repository.Remote.HospitalDoctorsRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookAppointmentsViewModel extends ViewModel {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    List<Calendar> availabilityDates = new ArrayList<>();
    List<Calendar> unAvailabilityDates = new ArrayList<>();
    HospitalDoctorsRepo hospitalDoctorsRepo;
    MutableLiveData<ModelUnAvailableDates> unAvailableDatesMutableLiveData = new MutableLiveData<>();

    public long getThresholdLimit(String time, String checkUpTime) {
        int timeDifference = getTimeDifference(time);
        int avgCheckupTime = Integer.valueOf(checkUpTime);
        Log.d("getThresholdLimit",timeDifference+"*"+avgCheckupTime+"*"+timeDifference/avgCheckupTime);
        return (long) timeDifference/avgCheckupTime;
    }

    private int getTimeDifference(String s) {
        String[] result,time1,time2;
        result = s.split("-");
        time1 = result[0].split(":");
        time2 = result[1].split(":");
        time1[0] = time1[0].replaceAll("\\s+", "");
        time2[0] = time2[0].replaceAll("\\s+", "");
        time1[1] = time1[1].replaceAll("\\s+", "");
        time2[1] = time2[1].replaceAll("\\s+", "");
        int h1,h2,m1,m2;
        h1 = Integer.valueOf(time1[0]);
        h2 = Integer.valueOf(time2[0]);
        m1 = Integer.valueOf(time1[1]);
        m2 = Integer.valueOf(time2[1]);

        if(h2>h1 && m2<m1){
            h2--;
            m2+=60;
        }
        return ((h2-h1)*60)+(m2-m1);
    }

    public void getAppointmentDates(BookAppointmentActivity bookAppointmentActivity, ModelDoctorInfo doctorProfileInfo) {
        hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
        hospitalDoctorsRepo.getUnavailableDates(doctorProfileInfo).observe(bookAppointmentActivity, new Observer<ArrayList<ModelDateTime>>() {
            @Override
            public void onChanged(ArrayList<ModelDateTime> modelDateTimes) {
                ArrayList<ModelDateTime> unavailableDates = new ArrayList<>(), CompletelyUnavailableDates, PartiallyUnavailableDates;
                CompletelyUnavailableDates = new ArrayList<>();
                PartiallyUnavailableDates = new ArrayList<>();
                if (modelDateTimes!=null) {
                    unavailableDates = modelDateTimes;
                    for (int i = 0; i < unavailableDates.size(); i++) {
                        if (unavailableDates.get(i).getUnavailableTimes().size() < doctorProfileInfo.getDoctorAvailabilityTime().size())
                            PartiallyUnavailableDates.add(unavailableDates.get(i));
                        else
                            CompletelyUnavailableDates.add(unavailableDates.get(i));
                    }
                    setUpDatePicker(unavailableDates, CompletelyUnavailableDates, PartiallyUnavailableDates,doctorProfileInfo);
                }
                else
                    setUpDatePicker(unavailableDates, CompletelyUnavailableDates, PartiallyUnavailableDates,doctorProfileInfo);
            }
        });
    }

    private void setUpDatePicker(ArrayList<ModelDateTime> unavailableDates, ArrayList<ModelDateTime> completelyUnavailableDates, ArrayList<ModelDateTime> partiallyUnavailableDates, ModelDoctorInfo doctorProfileInfo) {
        ModelUnAvailableDates unAvailableDatesModel = new ModelUnAvailableDates(getCompletelySlotUnavailableDates(completelyUnavailableDates),
                getAvailabilityDates(doctorProfileInfo),partiallyUnavailableDates);
        unAvailableDatesMutableLiveData.setValue(unAvailableDatesModel);
    }

    public Calendar[] getAvailabilityDates(ModelDoctorInfo doctorProfileInfo) {
        availabilityDates.clear();
        if (doctorProfileInfo.getAvailabilityType().equals("Monthly")) {
            Calendar calendar = Calendar.getInstance();
            int numberOfMonths = 0;
            while (numberOfMonths!=3) {
                calendar.roll(Calendar.MONTH,1);
                for (String day : doctorProfileInfo.getDoctorAvailability()) {
                    if (day.length() == 1)
                        day = "0" + day;

                    String month = "" +calendar.get(Calendar.MONTH);
                    if (month.equals("0"))
                        month = "12";
                    if (month.length()==1)
                        month = "0"+ month;

                    String a = day + "-" + month + "-"+calendar.get(Calendar.YEAR);
                    addDateToList(true,a);
                }
                Log.d("Year",""+calendar.get(Calendar.YEAR)+" Month : "+calendar.get(Calendar.MONTH));
                numberOfMonths++;
                if (calendar.get(Calendar.MONTH)== Calendar.JANUARY)
                    calendar.roll(Calendar.YEAR,1);
            }
        }

        if (doctorProfileInfo.getAvailabilityType().equals("Weekly")) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,2);
            Date endDate = calendar.getTime();
            calendar = Calendar.getInstance();
            int check = calendar.getTime().compareTo(endDate);
            while (check!=1){
                for (String dayOfWeek : doctorProfileInfo.getDoctorAvailability()) {
                    int week_day = getDayOfWeek(dayOfWeek);
                    if (week_day == calendar.get(Calendar.DAY_OF_WEEK)) {
                        String day = ""+calendar.get(Calendar.DAY_OF_MONTH);
                        if (day.length() == 1)
                            day = "0" + day;

                        String month = "" +(calendar.get(Calendar.MONTH)+1);
                        if (month.length()==1)
                            month = "0"+ month;

                        String a = day + "-" + month + "-"+calendar.get(Calendar.YEAR);

                        addDateToList(true, a);
                    }
                }
                calendar.add(Calendar.DAY_OF_YEAR,1);
                check = calendar.getTime().compareTo(endDate);
            }
        }

        return availabilityDates.toArray(new Calendar[availabilityDates.size()]);
    }

    public Calendar[] getCompletelySlotUnavailableDates(ArrayList<ModelDateTime> completelyUnavailableDates) {
        unAvailabilityDates.clear();
        for(ModelDateTime dateTime : completelyUnavailableDates){
            addDateToList(false,dateTime.getDate());
        }
        return unAvailabilityDates.toArray(new Calendar[unAvailabilityDates.size()]);
    }

    private void addDateToList(Boolean isAvailabilityDate, String a) {
        java.util.Date date = new Date();

        boolean status = false;
        sdf.setLenient(false);
        try {
            date = sdf.parse(a);
            status = true;
        } catch (ParseException e) {
            status = false;
            e.printStackTrace();
        }
        if (status) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (isAvailabilityDate)
                availabilityDates.add(cal);
            else
                unAvailabilityDates.add(cal);
        }
    }

    private int getDayOfWeek(String dayOfWeek) {
        if (dayOfWeek.equals("Sunday"))
            return Calendar.SUNDAY;
        if (dayOfWeek.equals("Monday"))
            return Calendar.MONDAY;
        if (dayOfWeek.equals("Tuesday"))
            return Calendar.TUESDAY;
        if (dayOfWeek.equals("Wednesday"))
            return Calendar.WEDNESDAY;
        if (dayOfWeek.equals("Thursday"))
            return Calendar.THURSDAY;
        if (dayOfWeek.equals("Friday"))
            return Calendar.FRIDAY;
        if (dayOfWeek.equals("Saturday"))
            return Calendar.SATURDAY;
        return 100;
    }

    public LiveData<ModelUnAvailableDates> getUnavailableDates() {
        return unAvailableDatesMutableLiveData;
    }

    public LiveData<String> bookAppointment(ModelBookAppointment appointment) {
        AppointmentsRepo appointmentsRepo = AppointmentsRepo.getInstance();
        return appointmentsRepo.bookAppointment(appointment);
    }
}
