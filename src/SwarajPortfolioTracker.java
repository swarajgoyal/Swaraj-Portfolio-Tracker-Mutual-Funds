import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

public class SwarajPortfolioTracker {
    Scanner scanner = new Scanner(System.in);
    HashMap <Integer,Integer> units = new HashMap <>();
    HashMap<Integer, String> MF_Option = new HashMap<>();
    PiyushPortfolioTracker(){
        MF_Option.put(122639, "Parag Parikh Flexi Cap Fund Direct Growth" );
        MF_Option.put(125354, "Axis Small Cap Fund Direct Growth" );
        MF_Option.put(120465, "Axis Bluechip Fund" );
    }

    double getFundPrice(int fundId) {
        try {
            final URL url = new URL("https://api.mfapi.in/mf/" + fundId);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String resp = String.join("", br.lines().toList());
            JSONObject jsonObject = new JSONObject(resp);
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject today = data.getJSONObject(0);
            String navString = today.getString("nav");
            Double nav = Double.parseDouble(navString);
            return nav;
        } catch (Exception e){
            System.out.println("NETWORK ERROR WHILE FETCHING NAV FOR MUTUAL FUND");
        }
        return 0;
    }

    void portfolio(){
        System.out.println("===============================================");
        System.out.println("PORTFOLIO.. \nLoading...");
        Double totalSum = Double.valueOf(0);
        for (int fundId: units.keySet()){
            Double nav = getFundPrice(fundId);
            int quantity = units.get(fundId);
            Double totalInvestment = quantity * nav;
            totalSum+=totalInvestment;
            System.out.println("FUND: "+MF_Option.get(fundId)+"|NAV(INR "+nav+")|UNITS("+quantity+")|TOTAL INVESTMENT:: "+totalInvestment);
        }
        System.out.println("TOTAL INVESTED ACROSS ALL FUNDS IS:: INR "+totalSum);
        System.out.println("===============================================");
    }

    void DisplayFunds(){
        System.out.println("Select Any Mutual Fund");
        for(int i : MF_Option.keySet())
            System.out.println(i + " -> " + MF_Option.get(i)+ " Existing units: "+units.getOrDefault(i,0).toString());
    }

    void AddUnits(){
        DisplayFunds();
        int select = scanner.nextInt();
        if(MF_Option.containsKey(select)) {
            System.out.println("Enter Your Purchased Units..");
            int addUnits = scanner.nextInt();
            if(addUnits >0)
                units.put(select, units.getOrDefault(select, 0) + addUnits);
            else {
                System.out.println("Value Less Than 1 Cannot Be Added..");
                return;
            }
        }
        else{
            System.out.println("you Entered Wrong Input..");
            return;
        }
        System.out.println("Units Added");
        return;
    }

    void RedeemUnits(){
        DisplayFunds();
        int select = scanner.nextInt();
        if(units.containsKey(select)) {
            System.out.println("Enter Units You Want To Redeem..");
            int redeemUnits = scanner.nextInt();
            if(redeemUnits <= units.get(select)) {
                units.put(select, units.get(select) - redeemUnits);
                if(units.get(select) == 0)
                    units.remove(select);
                System.out.println("You Redeemed " + redeemUnits + " units From " + MF_Option.get(select) + " Fund..");
            }
            else {
                System.out.println("Sorry, You Entered More Units Than Units Present ..");
                return;
            }
        }
        else{
            System.out.println("You Do Not Have Units To Redeem In This Fund..");
            return;
        }


    }

    void Menu(){
        try {
            System.out.println("Welcome!");
            System.out.println("Select One Option");
            System.out.println("1 -> Add Units");
            System.out.println("2 -> Redeem");
            System.out.println("3 -> View Portfolio");
            System.out.println("4 -> Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    AddUnits();
                    break;
                case 2:
                    RedeemUnits();
                    break;
                case 3:
                    portfolio();
                    break;
                case 4:
                    System.out.println("Bye...");
                    System.exit(1);
                    break;
                default:
                    System.out.println("INVALID option , please try again");
            }
        } catch (Exception e){
            System.out.println("There was an error in your input please try again");
            throw e;
        }
    }
    public void Run(){
        while (true){
            Menu();
        }
    }
}
