package com.hgb.hgbexample2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import appview.GraphicsView;
import hgb.HGBShared;
import model.GameBoardSetup;
import model.Shared;

public class MainActivity extends AppCompatActivity
{
   private final String TAG = this.getClass().getSimpleName();

   private GraphicsView graphicsView;
   private GameBoardSetup gameBoardSetup = null;
   private Shared shared = null;
   private HGBShared hgbShared = null;

   private boolean dbNumberCellsFlg = true;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      graphicsView = findViewById(R.id.graphicsview);

      if (createGameBoard() == false)
      {
         Toast.makeText(this, "Game failed to initiate", Toast.LENGTH_LONG).show();
         finish();
      }
   }

   /**
    * Create the game board
    *
    * @return
    */
   private boolean createGameBoard()
   {
      if (gameBoardSetup != null) { gameBoardSetup = null; }
      gameBoardSetup = new GameBoardSetup();

      if (shared != null) { shared = null; }
      shared = gameBoardSetup.gameBoardInitialize();
      if (shared == null) return false;

      hgbShared = shared.getHGBShared();
      if (hgbShared == null) return false;

      graphicsView.setShared(shared);
      graphicsView.setPath();
      graphicsView.localInit();
      graphicsView.invalidate();
      return true;
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      try
      {
         switch (item.getItemId())
         {
             default:
               return super.onOptionsItemSelected(item);
         }
      }
      catch(Exception excp)
      {
         Log.e("FatalError:  ", excp.toString());
         return false;
      }
   }

}
