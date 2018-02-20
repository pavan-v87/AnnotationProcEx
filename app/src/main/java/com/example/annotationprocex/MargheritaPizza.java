package com.example.annotationprocex;

import com.example.Factory;
import com.example.KpiLogEnd;
import com.example.KpiLogStart;
import com.example.LogConstants;

@Factory(
    id = "Margherita",
    type = Meal.class
)
public class MargheritaPizza implements Meal {

  @Override public float getPrice() {
    return 6f;
  }

  @KpiLogEnd(event = "get_price_duration", category = "kpi")
  @KpiLogStart(event = "get_price_duration", category = "kpi")
  private int getSliceCount() {
    return 10;
  }
}