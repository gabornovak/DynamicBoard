package hu.gabornovak.dynamicboard.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import hu.gabornovak.dynamicboard.DynamicBoardLayout;
import hu.gabornovak.dynamicboard.board.Board;
import hu.gabornovak.dynamicboard.board.BoardItem;
import hu.gabornovak.dynamicboard.board.GridPosition;
import hu.gabornovak.dynamicboard.board.Size;
import hu.gabornovak.dynamicboard.option.DeleteOption;

public class MainActivity extends AppCompatActivity {
    private DynamicBoardLayout boardLayout;
    private String[] values = new String[]{"Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
            "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
            "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
            "Android", "iPhone", "WindowsMobile"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setBackgroundDrawable(null);

        boardLayout = (DynamicBoardLayout) findViewById(R.id.board);

        boardLayout.setOnBoardInitializedListener(new Board.OnBoardInitializedListener() {
            @Override
            public void onBoardInitialized(Size boardSize) {
                BoardItem boardItem1 = new DynamicBoardItem(MainActivity.this, "Marketing", "+22% of target", 1234000, R.drawable.chart1);
                BoardItem boardItem2 = new DynamicBoardItem(MainActivity.this, "Conversion", "+12.3% of target", 1000000, R.drawable.chart2);
                BoardItem boardItem3 = new DynamicBoardItem(MainActivity.this, "Impressions", "+12.3% of target", 95030000, R.drawable.chart2);
                BoardItem boardItem4 = new DynamicBoardItem(MainActivity.this, "Sales", "+20.3% of target", 15030000, R.drawable.chart1);
                BoardItem boardItem5 = new ListBoardItem(MainActivity.this, values);

                boardItem1.setMinSize(new Size(2, 1));
                boardItem2.setMinSize(new Size(2, 2));
                boardItem1.addOption(new DeleteOption());

                boardLayout.addBoardItem(boardItem1, new GridPosition(0, 0), new Size(2, 2));
                boardLayout.addBoardItem(boardItem2, new GridPosition(2, 0), new Size(3, 3));
                boardLayout.addBoardItem(boardItem3, new GridPosition(0, 2), new Size(2, 3));
                boardLayout.addBoardItem(boardItem4, new GridPosition(0, 5), new Size(2, 2));
                boardLayout.addBoardItem(boardItem5, new GridPosition(2, 3), new Size(3, 4));

                System.out.println("Board size: " + boardSize);
            }
        });
    }
}
