package com.junhyeoklee.som.ui.adapter;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.junhyeoklee.som.Alarmio;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.alarm.AlarmData;
import com.junhyeoklee.som.ui.dialog.AlertDialog;
import com.junhyeoklee.som.ui.view.AestheticTimeSheetPickerDialog;
import com.junhyeoklee.som.ui.view.AlarmDaySwitch;
import com.junhyeoklee.som.ui.view.ProgressLineView;
import com.junhyeoklee.som.util.AlarmFormatUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import io.reactivex.functions.Consumer;
import me.jfenn.androidutils.DimenUtils;
import me.jfenn.timedatepickers.dialogs.PickerDialog;
import me.jfenn.timedatepickers.views.LinearTimePickerView;

public class AlarmsAdapter extends RecyclerView.Adapter {

    private Alarmio alarmio;
    private RecyclerView recycler;
    private SharedPreferences prefs;
    private AlarmManager alarmManager;
    private FragmentManager fragmentManager;
    private List<AlarmData> alarms;
    private int colorAccent = Color.WHITE;
    private int colorForeground = Color.TRANSPARENT;
    private int textColorPrimary = Color.WHITE;

    private int expandedPosition = -1;

    public AlarmsAdapter(Alarmio alarmio, RecyclerView recycler, FragmentManager fragmentManager) {
        this.alarmio = alarmio;
        this.recycler = recycler;
        this.prefs = alarmio.getPrefs();
        this.fragmentManager = fragmentManager;
        alarmManager = (AlarmManager) alarmio.getSystemService(Context.ALARM_SERVICE);
        alarms = alarmio.getAlarms();

    }

    public void setColorAccent(int colorAccent) {
        this.colorAccent = colorAccent;
        notifyDataSetChanged();
    }

    public void setColorForeground(int colorForeground) {
        this.colorForeground = colorForeground;
        if (expandedPosition > 0)
            notifyItemChanged(expandedPosition);
    }

    public void setTextColorPrimary(int colorTextPrimary) {
        this.textColorPrimary = colorTextPrimary;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new AlarmViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final AlarmViewHolder alarmHolder = (AlarmViewHolder) holder;
        final boolean isExpanded = position == expandedPosition;
        AlarmData alarm = getAlarm(position);

        alarmHolder.enable.setOnCheckedChangeListener(null);
        alarmHolder.enable.setChecked(alarm.isEnabled);
        alarmHolder.enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getAlarm(alarmHolder.getAdapterPosition()).setEnabled(alarmio, alarmManager, b);

                Transition transition = new AutoTransition();
                transition.setDuration(200);
                TransitionManager.beginDelayedTransition(recycler, transition);

                notifyDataSetChanged();
            }
        });

        alarmHolder.time.setText(AlarmFormatUtils.formatShort(alarmio, alarm.time.getTime()));
        alarmHolder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());

                new AestheticTimeSheetPickerDialog(view.getContext(), alarm.time.get(Calendar.HOUR_OF_DAY), alarm.time.get(Calendar.MINUTE))
                        .setListener(new PickerDialog.OnSelectedListener<LinearTimePickerView>() {
                            @Override
                            public void onSelect(PickerDialog<LinearTimePickerView> dialog, LinearTimePickerView view) {
                                AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                                alarm.time.set(Calendar.HOUR_OF_DAY, view.getHourOfDay());
                                alarm.time.set(Calendar.MINUTE, view.getMinute());
                                alarm.setTime(alarmio, alarmManager, alarm.time.getTimeInMillis());

                                notifyItemChanged(alarmHolder.getAdapterPosition());
                            }

                            @Override
                            public void onCancel(PickerDialog<LinearTimePickerView> dialog) {
                            }
                        })
                        .show();
            }
        });

        alarmHolder.nextTime.setVisibility(alarm.isEnabled ? View.VISIBLE : View.GONE);

        Calendar nextAlarm = alarm.getNext();
        if (alarm.isEnabled && nextAlarm != null) {
            Date nextAlarmTime = alarm.getNext().getTime();

            // minutes in a week: 10080
            // maximum value of an integer: 2147483647
            // we do not need to check this int cast
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(nextAlarm.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());

            alarmHolder.nextTime.setText(String.format(alarmio.getString(R.string.title_alarm_next),
                    AlarmFormatUtils.format(nextAlarmTime, "MMMM d"), AlarmFormatUtils.formatUnit(alarmio, minutes)));
        }

        alarmHolder.indicators.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        if (isExpanded) {
            alarmHolder.repeat.setOnCheckedChangeListener(null);
            alarmHolder.repeat.setChecked(alarm.isRepeat());
            alarmHolder.repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                    for (int i = 0; i < 7; i++) {
                        alarm.days[i] = b;
                    }
                    alarm.setDays(alarmio, alarm.days);

                    Transition transition = new AutoTransition();
                    transition.setDuration(150);
                    TransitionManager.beginDelayedTransition(recycler, transition);

                    notifyDataSetChanged();
                }
            });

            alarmHolder.days.setVisibility(alarm.isRepeat() ? View.VISIBLE : View.GONE);

            AlarmDaySwitch.OnCheckedChangeListener listener = new AlarmDaySwitch.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(AlarmDaySwitch daySwitch, boolean b) {
                    AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                    alarm.days[alarmHolder.days.indexOfChild(daySwitch)] = b;
                    alarm.setDays(alarmio, alarm.days);

                    if (!alarm.isRepeat()) {
                        notifyItemChanged(alarmHolder.getAdapterPosition());
                    } else {
                        // if the view isn't going to change size in the recycler,
                        //   then I can just do this (prevents the background flickering as
                        //   the recyclerview attempts to smooth the transition)
                        bindViewHolder(alarmHolder, alarmHolder.getAdapterPosition());
                    }
                }
            };

            for (int i = 0; i < 7; i++) {
                AlarmDaySwitch alarmDaySwitch = (AlarmDaySwitch) alarmHolder.days.getChildAt(i);
                alarmDaySwitch.setChecked(alarm.days[i]);
                alarmDaySwitch.setOnCheckedChangeListener(listener);

                switch (i) {
                    case 0:
                        alarmDaySwitch.setText("일");
                        break;
                    case 1:
                        alarmDaySwitch.setText("월");
                        break;
                    case 2:
                        alarmDaySwitch.setText("화");
                        break;
                    case 3:
                        alarmDaySwitch.setText("수");
                        break;
                    case 4:
                        alarmDaySwitch.setText("목");
                        break;
                    case 5:
                        alarmDaySwitch.setText("금");
                        break;
                    case 6:
                        alarmDaySwitch.setText("토");
                        break;

                }
            }
        }

        alarmHolder.expandImage.animate().rotationX(isExpanded ? 180 : 0).start();
        alarmHolder.delete.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        alarmHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                new AlertDialog(view.getContext())
                        .setContent(alarmio.getString(R.string.msg_delete_confirmation))
                        .setListener(new AlertDialog.Listener() {
                            @Override
                            public void onDismiss(AlertDialog dialog, boolean ok) {
                                if (ok)
                                    alarmio.removeAlarm(getAlarm(alarmHolder.getAdapterPosition()));
                            }
                        })
                        .show();
            }
        });

        alarmHolder.repeat.setTextColor(textColorPrimary);
        alarmHolder.delete.setTextColor(textColorPrimary);
//            alarmHolder.vibrateImage.setColorFilter(textColorPrimary);
        alarmHolder.expandImage.setColorFilter(textColorPrimary);

        int visibility = isExpanded ? View.VISIBLE : View.GONE;
        if (visibility != alarmHolder.extra.getVisibility()) {
            alarmHolder.extra.setVisibility(visibility);
            Aesthetic.Companion.get()
                    .colorPrimary()
                    .take(1)
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), isExpanded ? integer : colorForeground, isExpanded ? colorForeground : integer);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    alarmHolder.itemView.setBackgroundColor((int) animation.getAnimatedValue());
                                }
                            });
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    alarmHolder.itemView.setBackgroundColor(isExpanded ? colorForeground : Color.TRANSPARENT);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                            animator.start();
                        }
                    });

            ValueAnimator animator = ValueAnimator.ofFloat(isExpanded ? 0 : DimenUtils.dpToPx(2), isExpanded ? DimenUtils.dpToPx(2) : 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewCompat.setElevation(alarmHolder.itemView, (float) animation.getAnimatedValue());
                }
            });
            animator.start();
        } else {
            alarmHolder.itemView.setBackgroundColor(isExpanded ? colorForeground : Color.TRANSPARENT);
            ViewCompat.setElevation(alarmHolder.itemView, isExpanded ? DimenUtils.dpToPx(2) : 0);
        }

        alarmHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                Transition transition = new AutoTransition();
                transition.setDuration(250);
                TransitionManager.beginDelayedTransition(recycler, transition);

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position ;
    }

    @Override
    public int getItemCount() {
        return  alarms.size();
    }


    private AlarmData getAlarm(int position) {
        return alarms.get(position);
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private SwitchCompat enable;
        private TextView time;
        private TextView nextTime;
        private View extra;
        private AppCompatCheckBox repeat;
        private LinearLayout days;
        //        private View vibrate;
//        private ImageView vibrateImage;
        private ImageView expandImage;
        private TextView delete;
        private View indicators;

        public AlarmViewHolder(View v) {
            super(v);
            enable = v.findViewById(R.id.enable);
            time = v.findViewById(R.id.time);
            nextTime = v.findViewById(R.id.nextTime);
            extra = v.findViewById(R.id.extra);
            repeat = v.findViewById(R.id.repeat);
            days = v.findViewById(R.id.days);
//            vibrate = v.findViewById(R.id.vibrate);
//            vibrateImage = v.findViewById(R.id.vibrateImage);
            expandImage = v.findViewById(R.id.expandImage);
            delete = v.findViewById(R.id.delete);
            indicators = v.findViewById(R.id.indicators);
        }
    }
}
