package net.sfdc.ci;

/*
FR:
1. View balance
2. Withdraw cash
3. Insert cash
4. Change pin

Flow:
1. Insert debit card, enter pin, perform operation.
2. Cash handler: Chain of responsibility
3. State pattern to handle various states


Classes:
1. AtmMachine
2. State interface
3. IdleState, CardInsertedState, CardValidatedState
4. CashHandler class
5. Account class
5. AccountManager class
 */

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}
