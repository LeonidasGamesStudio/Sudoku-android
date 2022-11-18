package com.example.sudoku.board.gridGeneration

import java.util.Random as rand

class SudokuPresetsDifficulty {
    private val easyPuzzles = arrayOf(
        "000530001300089002900001045002007400009000100008200500560400003800150004100078000",
        "800021000036000482005400016070100005000000000400007030750003200961000350000860001",
        "064070100059800006100000090000750300430060017006043000090000001700002450005010680",
        "730008050049300020000007103000004080920060015050100000603200000070009240010800069",
        "800009150020000006050306084612000000004000300000000428130207090200000010098500003",
        "000009305080000900400170020800900003093504810700003009060028004002000080108300000",
        "000075600360002070080604020600009000801000503000300002070406010090700048002910000",
        "010200060003100000200076039057600001008000300300001690830590002000007800040002050",
        "060700000900000748005001006610004800407080109002900037100200900594000003000005070",
        "450003000000870300237090000905008000806040509000100408000020837001084000000500041"
    )
    private val mediumPuzzles = arrayOf(
        "000520001000048502400000730250061070000000000070950064037000006104270000800035000",
        "000360014000002903001708000946081000000000000000240168000804200502100000360027000",
        "047001290000400001300070000406700800500602007003005904000010003700004000039200740",
        "009300000004010000038059104000205007043000280900407000805720410000060700000001600",
        "036004080209050000070000900090402000402103706000507020004000060000020804010700530",
        "000070200000802790003000068200150040080406070040037006460000900017305000002090000",
        "002040610500002009700050280000804920000000000027109000075010008800400002041030500",
        "956007080100002050000160300003000560000608000071000400008016000060700001010500296",
        "600900000805300070340200100500006000286000597000500003001002035050003406000007002",
        "480520000091000080700001600000210058007000100120053000003800007040000210000032049"
    )

    private val hardPuzzles = arrayOf(
        "065009040018060009000041080000200051004000200520007000050170000600080310040500870",
        "000501067090080400050006028400058000010000050000720004230400090001060040640805000",
        "590002300020100000400009082010005920000908000086700040140300006000006070005200093",
        "000720406030001050006300008050007004008403500400900020700002100010500080302074000",
        "370000060006850040008076001450009100000030000003400085500720800010068900060000027",
        "020758001000930000704000000270890010006000800080067052000000209000073000300489060",
        "574000106620005000109060000000670002800000004700098000000050901000300087207000463",
        "080020154102400000030000000093004200540000089007900340000000070000003502675010030",
        "012706900000082000900040000340100802009030500701008039000090007000810000008205390",
        "580701060304000005100000000700305000051208690000104008000000006600000804010806079"
    )

    private val expertPuzzles = arrayOf(
        "040600005062700094900000100280010400005000900006020078004000007370006240600004030",
        "703492000800060200045008300900000100070000050006000004007500480008010005000836701",
        "900007060073860000005023000051000007309000401700000290000630900000048370030700004",
        "020070900003000800060038005004890001302000609900021300400560090006000500007080020",
        "000607514503041000000000900020000605307060108605000090008000000000420807764105000",
        "810400560000050001062000407000905030090000050070806000907000210100090000058004079",
        "009010002000000405203080610090400200307000501002001060014020306906000000700030900",
        "090184007061000004000700300030090801080000050102050090008003000600000230300612070",
        "570092000009000057000010203000486070000201000080937000107060000390000700000170085",
        "900200100006008039010004008100003290002000300087900006600400050250600900001009002"
    )
    fun returnPreset(difficultySelect: Int): String {

        return when (difficultySelect) {
            1 -> easyPuzzles[rand().nextInt(easyPuzzles.size)]
            2 -> mediumPuzzles[rand().nextInt(mediumPuzzles.size)]
            3 -> hardPuzzles[rand().nextInt(hardPuzzles.size)]
            4 -> "827154396965327148341689752593468271472513689618972435786235914154796823239841560"
            //else is used for testing
            else -> "700305040040060130000402706009040000650970024400500697025704000394106205067009413"
        }
    }
}