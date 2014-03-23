package Backup.Server;

import java.io.IOException;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 */

//                              .NuLYr
//                               L@B7N@:
//                                r@  2@
//                               ,@:  @8
//                             .O@,  XB
//                           :@BU   XB                                                              .
//                        iGB@r    @B                                                         LBB@BBOBB@B@F;
//                     .k@BB7     Y@X                                                        BBE:         ,JM@BL
//                  r@Bk.      uB@                                                         @F                iN@Mi
//                7B@:      .N@8.                                                         @S     .:             ;B@:
//               @B:       BB5                                                            B      @B           L@  ,@i
//             :@j       L@5                                                              @:        .OLi7:iu: .i   G@
//            r@,       B@                                                                B.         SB7:LB@      .@J
//           :@.       B@                                                                U@    rB1U;, :juL:   :v0B@r
//           @7       :@                                                                 @Y     1@vS0BXGOMO@O@ZXr.
//          0B        @0                                                                ,B      @.
//          BL        Bi                                                                O@     @E
//          @,        @:                                                                @,    :B
//          B:        BB                                                               @B     B@
//          @u        ,Bi                                                             OB      @i
//          u@         rBu                                                           @B      ,B
//          BO         .B@;                                                       .@B       7@
//          .B1          i@BN,                                                  :B@r        XB
//           .BO           .u@BBvJFEOMB@M@B@B@B@B@M@BMO0Fj:.                 :k@BY          8@
//             B@              iSuYii..             ..iiJuZO@B@MZu7:, ..:7EB@B1.            @O
//              j@q                                            :r1PBBBM@8P7,                B0
//                @Bk                                                                       @L
//                 .O@0.                                                                    Bi
//                    XB@r                                                                  @
//                      i@BM.                                                              rB
//                         L@.                      I AM A STEGOSAURUS                     O@
//                          Bi                                                             @i
//                          @P                                                            iB
//                          i@                                                            B@
//                           BB                                                          i@J@r
//                            BF   PB    u8                                              @r :@B
//                             BM 7B,    B@                             .               @1 .  @B.
//                              1@B,     @L                            MB    OB       ,@B  ,.. 1Bi
//                               Br      B:                            J@     @F     Z@:@2 .:., 7Br
//                              BZ       @                             7B      @   0BE  .@: .:., iB7
//                             P@        BMJi                          :@      Y@P@u     M@ ..:., iB:
//                            .@        .@.7E@M8Li.                    ,B       BO        B: ,.:., JB
//                            @Z        .B    @BJ8@B@M8uUi:i7r7ru2kNM8MB@       ,B        @k .:.:.. @B
//                           7B         .@    Bi   .,r7UY@BuuS25u277i, rB        @G       Z@ ,.:.:.. @7
//                           BS          B   ,@ ..,.. .  X@            r@         @       XB .:.:.:. J@
//                          ,@           @.  5B .,.:.:.. 8B            qB         BM      X@ ,.:.:.:  B7
//                          @M           Br  B@  .,.,..  X@            BB         :B      @M .:.:.:., @B
//                          B:           @u  :B@r,   ..rU@M            @v          @7     B7 ,.:.:.:. 7@
//                         i@            8@    r@B@B@B@M1.             B.          P@     @Z. ..,.,.. :B
//                         @B            rB        .                  i@           ,B      0BB7: . .,LB@
//                         .@B,        .5B@                           @M            @:       7BB@B@B@G7
//                           uB@kY;vuMB@U.                            BF            BM           .
//                             .vUFUj:                                :B@r        :8@i
//                                                                      :BB@k1ukM@Bv
//                                                                          ::i,

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: client <srvc_port> <mcast_addr> <mcast_port>");
            System.exit(0);
        }

        new ServerThreadMulticast(args[1], args[2]).start();
    }
}
