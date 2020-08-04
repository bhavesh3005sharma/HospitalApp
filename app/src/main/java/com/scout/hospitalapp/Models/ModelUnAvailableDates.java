package com.scout.hospitalapp.Models;

import java.util.ArrayList;
import java.util.Calendar;

public class ModelUnAvailableDates {
    Calendar[] CompletelySlotUnavailableDates;
    Calendar[] AvailableDates;
    ArrayList<ModelDateTime> PartiallyUnavailableDates;

    public ModelUnAvailableDates(Calendar[] completelySlotUnavailableDates, Calendar[] availableDates, ArrayList<ModelDateTime> partiallyUnavailableDates) {
        CompletelySlotUnavailableDates = completelySlotUnavailableDates;
        AvailableDates = availableDates;
        PartiallyUnavailableDates = partiallyUnavailableDates;
    }

    public Calendar[] getCompletelySlotUnavailableDates() {
        return CompletelySlotUnavailableDates;
    }

    public Calendar[] getAvailableDates() {
        return AvailableDates;
    }

    public ArrayList<ModelDateTime> getPartiallyUnavailableDates() {
        return PartiallyUnavailableDates;
    }
}
