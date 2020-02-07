package com.example.quicoffee;

import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseUtill {
    private static FirebaseDatabase databaseReference;
    public FireBaseUtill(){
        databaseReference = getInstance();
    }
    private static FirebaseDatabase getInstance()
    {
        if (databaseReference == null) {
            try {
                databaseReference = FirebaseDatabase.getInstance();
            }catch(Exception e){
                e.getMessage();
            }
        }
        return databaseReference;
    }

    public void AddShopToUser(User user){
        DatabaseReference shopReference = databaseReference.getReference(Global_Variable.TABLE_SHOP);
        String id = shopReference.push().getKey();
        user.getShop().setID(id);
        DatabaseReference userReference = databaseReference.getReference(Global_Variable.TABLE_USERS);
        userReference.child(id).setValue(user);
    }
    public DatabaseReference getRefrencesUsers(){
        return databaseReference.getReference(Global_Variable.TABLE_USERS);
    }
    /*public DatabaseReference getRefrencesScoresSheet(){
        return databaseReference.getReference(Global_Variable.SCORE_SHEET_TABLE_NAME);
    }*/
    /*public ScoreSheetModel saveScoreSheet(String username, int scoreAchieved, LatLng location){
        DatabaseReference databaseReference = FireBaseUtill.databaseReference.getReference(Global_Variable.SCORE_SHEET_TABLE_NAME);
        String id = databaseReference.push().getKey();
        ScoreSheetModel scoreSheetModel = new ScoreSheetModel(username,scoreAchieved,location);
        databaseReference.child(id).setValue(scoreSheetModel);
        return scoreSheetModel;
    }*/
}
