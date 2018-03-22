package com.example.jeandan.smart_task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class LaunchActivity extends AppCompatActivity {


    private ImageView spyImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        getSupportActionBar().hide();

        spyImageView = (ImageView) findViewById(R.id.spy_image);

        //On crée les animations que nous allons utiliser
        final Animation animApparition = AnimationUtils.loadAnimation(getBaseContext(),R.anim.appearance_effect);

        genereAnim(animApparition,spyImageView);

        quitteAnim(animApparition, spyImageView);

    }

    public void genereAnim(Animation animation, final ImageView myText)
    {
        //On applique les animations aux images
        myText.startAnimation(animation);
    }

    public void quitteAnim(final Animation animFin, final ImageView textFin)
    {
        final Animation animQuitter = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);
        animFin.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            // à la fin de l'animation, on  lance l'animation "animQuitter" et on va à l'activité suivante
            public void onAnimationEnd(Animation animation)
            {
                textFin.startAnimation(animQuitter);
                finish();

                demarrageApplication();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
    }

    public void demarrageApplication(){
        Intent demarrage;

        demarrage = new Intent(LaunchActivity.this, MainActivity.class);

        startActivity(demarrage);
    }
}
