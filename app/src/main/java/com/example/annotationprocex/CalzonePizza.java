package com.example.annotationprocex;

import com.example.Factory;
import com.example.KpiLog;
import com.example.KpiLogEnd;
import com.example.KpiLogStart;
import com.example.LogConstants;

@Factory(
    id = "Calzone",
    type = Meal.class
)
public class CalzonePizza implements Meal {

  @KpiLogStart(event = "get_price_duration", category = "kpi")
  @Override public float getPrice() {
    return 8.5f;
  }

  @KpiLogEnd(event = "get_price_duration", category = "kpi")
  public String getMealEnd() {
    return "CalzonePizza";
  }

  @KpiLogStart (event = "get_calories_duration", category = "kpi")
  public String getCalories() {
    return "CalzonePizza";
  }
  @KpiLogEnd (event = "get_calories_duration", category = "kpi")
  public String getCaloriesEnd() {
    return "CalzonePizza";
  }
}