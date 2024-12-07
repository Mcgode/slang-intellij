// Generated by JFlex 1.9.1 http://jflex.de/  (tweaked for IntelliJ platform)
// source: Slang.flex

package slang.plugin.psi;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static slang.plugin.psi.SlangTypes.*;


public class SlangLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int MULTILINE_COMMAND_STATE = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1, 1
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\5\u0100\1\u0200\1\u0300\1\u0100\5\u0400\1\u0500\1\u0600"+
    "\1\u0700\5\u0100\1\u0800\1\u0900\1\u0a00\1\u0b00\1\u0c00\1\u0d00"+
    "\1\u0e00\3\u0100\1\u0f00\17\u0100\1\u1000\165\u0100\1\u0600\1\u0100"+
    "\1\u1100\1\u1200\1\u1300\1\u1400\54\u0100\10\u1500\37\u0100\1\u0a00"+
    "\4\u0100\1\u1600\10\u0100\1\u1700\2\u0100\1\u1800\1\u1900\1\u1400"+
    "\1\u0100\1\u0500\1\u0100\1\u1a00\1\u1700\1\u0900\3\u0100\1\u1300"+
    "\1\u1b00\114\u0100\1\u1c00\1\u1300\153\u0100\1\u1d00\11\u0100\1\u1e00"+
    "\1\u1400\6\u0100\1\u1300\u0f16\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\2\3\1\4\22\0\1\1\2\0"+
    "\1\5\1\0\1\6\1\7\1\0\1\10\1\11\1\12"+
    "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\2\22"+
    "\1\23\1\22\1\24\1\22\1\25\1\22\1\26\1\27"+
    "\1\30\1\31\1\32\1\33\1\0\4\34\1\35\1\36"+
    "\5\37\1\40\10\37\1\41\2\37\1\42\2\37\1\43"+
    "\1\44\1\45\1\46\1\47\1\0\1\50\1\51\1\52"+
    "\1\53\1\54\1\55\1\37\1\56\1\57\2\37\1\60"+
    "\1\61\1\62\1\63\1\64\1\37\1\65\1\66\1\67"+
    "\1\70\1\71\1\72\1\73\1\74\1\37\1\75\1\76"+
    "\1\77\1\100\6\0\1\3\32\0\1\1\u01bf\0\12\22"+
    "\206\0\12\22\306\0\12\22\234\0\12\22\166\0\12\22"+
    "\140\0\12\22\166\0\12\22\106\0\12\22\u0116\0\12\22"+
    "\106\0\12\22\346\0\1\1\u015f\0\12\22\46\0\12\22"+
    "\u012c\0\12\22\200\0\12\22\246\0\12\22\6\0\12\22"+
    "\266\0\12\22\126\0\12\22\206\0\12\22\6\0\12\22"+
    "\246\0\13\1\35\0\2\3\5\0\1\1\57\0\1\1"+
    "\240\0\1\1\u01cf\0\12\22\46\0\12\22\306\0\12\22"+
    "\26\0\12\22\126\0\12\22\u0196\0\12\22\6\0\u0100\101"+
    "\240\0\12\22\206\0\12\22\u012c\0\12\22\200\0\12\22"+
    "\74\0\12\22\220\0\12\22\166\0\12\22\146\0\12\22"+
    "\206\0\12\22\106\0\12\22\266\0\12\22\u0164\0\62\22"+
    "\100\0\12\22\266\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[7936];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\1\2\1\1\1\3\1\4\1\5\1\6"+
    "\1\7\1\10\1\11\1\12\1\1\1\13\2\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\23\1\1\1\24"+
    "\1\25\17\22\1\26\1\27\1\30\1\31\3\32\1\33"+
    "\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43"+
    "\1\44\1\0\1\45\1\0\1\46\3\0\1\2\1\47"+
    "\13\22\1\50\11\22\1\51\1\52\1\41\3\0\1\41"+
    "\1\14\1\53\1\54\14\22\1\55\2\22\1\56\6\22"+
    "\1\57\1\60\3\22\1\61\1\62\1\22\1\63\2\22"+
    "\1\64\10\22\1\65\1\66\1\67\1\22\1\70\1\71"+
    "\2\22\1\72\14\22\1\73\1\74\1\75\3\22\1\76"+
    "\4\22\1\77\4\22\1\100\1\101\2\22\1\102\3\22"+
    "\1\103\1\104\3\22\1\105\1\106\1\107\1\110\1\111";

  private static int [] zzUnpackAction() {
    int [] result = new int[196];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\102\0\204\0\306\0\u0108\0\u014a\0\u018c\0\204"+
    "\0\204\0\u01ce\0\u0210\0\204\0\u0252\0\u0294\0\u02d6\0\u0318"+
    "\0\u035a\0\u039c\0\204\0\u03de\0\204\0\u0420\0\u0462\0\204"+
    "\0\u04a4\0\204\0\u04e6\0\u0528\0\u056a\0\u05ac\0\u05ee\0\u0630"+
    "\0\u0672\0\u06b4\0\u06f6\0\u0738\0\u077a\0\u07bc\0\u07fe\0\u0840"+
    "\0\u0882\0\u08c4\0\204\0\u0906\0\204\0\204\0\u0948\0\204"+
    "\0\u098a\0\204\0\204\0\204\0\204\0\204\0\204\0\u09cc"+
    "\0\204\0\u0a0e\0\204\0\u0a50\0\204\0\u0a92\0\204\0\u0ad4"+
    "\0\u0b16\0\u04a4\0\u04a4\0\204\0\u0b58\0\u0b9a\0\u0bdc\0\u0c1e"+
    "\0\u0c60\0\u0ca2\0\u0ce4\0\u0d26\0\u0d68\0\u0daa\0\u0dec\0\u0e2e"+
    "\0\u0e70\0\u0eb2\0\u0ef4\0\u0f36\0\u0f78\0\u0fba\0\u0ffc\0\u103e"+
    "\0\u1080\0\204\0\204\0\204\0\u10c2\0\u1104\0\u1146\0\u1188"+
    "\0\u11ca\0\204\0\204\0\u120c\0\u124e\0\u1290\0\u12d2\0\u1314"+
    "\0\u1356\0\u1398\0\u13da\0\u141c\0\u145e\0\u14a0\0\u14e2\0\u1524"+
    "\0\u1566\0\u15a8\0\u0462\0\u15ea\0\u162c\0\u166e\0\u16b0\0\u16f2"+
    "\0\u1734\0\204\0\u0462\0\u1776\0\u17b8\0\u17fa\0\u0462\0\u0462"+
    "\0\u183c\0\u0462\0\u187e\0\u18c0\0\u0462\0\u1902\0\u1944\0\u1986"+
    "\0\u19c8\0\u1a0a\0\u1a4c\0\u1a8e\0\u1ad0\0\u0462\0\u1b12\0\u0462"+
    "\0\u1b54\0\u0462\0\u0462\0\u1b96\0\u1bd8\0\u0462\0\u1c1a\0\u1c5c"+
    "\0\u1c9e\0\u1ce0\0\u1d22\0\u1d64\0\u1da6\0\u1de8\0\u1e2a\0\u1e6c"+
    "\0\u1eae\0\u1ef0\0\u0462\0\u0462\0\u0462\0\u1f32\0\u1f74\0\u1fb6"+
    "\0\u0462\0\u1ff8\0\u203a\0\u207c\0\u20be\0\u0462\0\u2100\0\u2142"+
    "\0\u2184\0\u21c6\0\u0462\0\u0462\0\u2208\0\u224a\0\u0462\0\u228c"+
    "\0\u22ce\0\u2310\0\u0462\0\u0462\0\u2352\0\u2394\0\u23d6\0\u0462"+
    "\0\u0462\0\u0462\0\u0462\0\u0462";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[196];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\4\4\1\5\1\6\1\7\1\10\1\11\1\12"+
    "\1\13\1\14\1\15\1\16\1\17\1\20\5\21\1\22"+
    "\1\23\1\24\1\25\1\26\1\3\7\27\1\30\1\31"+
    "\1\32\1\33\2\27\1\34\1\35\1\36\1\37\1\40"+
    "\1\41\1\42\2\27\1\43\1\44\1\45\1\27\1\46"+
    "\1\47\1\50\1\51\1\52\2\27\1\53\1\54\1\55"+
    "\1\56\1\3\2\57\1\60\7\57\1\61\67\57\103\0"+
    "\4\4\130\0\1\62\77\0\1\63\101\0\1\64\101\0"+
    "\1\65\101\0\1\66\101\0\1\67\70\0\6\70\66\0"+
    "\1\71\4\0\1\72\11\0\1\73\66\0\1\70\1\0"+
    "\6\21\7\0\1\74\3\0\1\75\1\76\11\0\1\74"+
    "\13\0\1\75\2\0\1\76\24\0\1\70\1\0\6\21"+
    "\7\0\1\74\3\0\1\75\12\0\1\74\13\0\1\75"+
    "\37\0\1\77\103\0\1\100\103\0\1\101\67\0\6\27"+
    "\6\0\7\27\4\0\26\27\6\0\1\102\1\103\1\102"+
    "\1\103\126\0\1\104\70\0\6\27\6\0\7\27\4\0"+
    "\14\27\1\105\11\27\25\0\6\27\6\0\7\27\4\0"+
    "\11\27\1\106\2\27\1\107\11\27\25\0\6\27\6\0"+
    "\7\27\4\0\14\27\1\110\11\27\25\0\6\27\6\0"+
    "\7\27\4\0\1\27\1\111\11\27\1\112\10\27\1\113"+
    "\1\27\25\0\6\27\6\0\7\27\4\0\1\27\1\114"+
    "\7\27\1\115\7\27\1\116\4\27\25\0\6\27\6\0"+
    "\7\27\4\0\1\27\1\117\24\27\25\0\6\27\6\0"+
    "\7\27\4\0\13\27\1\120\12\27\25\0\6\27\6\0"+
    "\7\27\4\0\1\27\1\121\12\27\1\122\11\27\25\0"+
    "\6\27\6\0\7\27\4\0\21\27\1\123\4\27\25\0"+
    "\6\27\6\0\7\27\4\0\16\27\1\124\7\27\25\0"+
    "\6\27\6\0\7\27\4\0\20\27\1\125\5\27\25\0"+
    "\6\27\6\0\7\27\4\0\16\27\1\126\7\27\25\0"+
    "\6\27\6\0\7\27\4\0\10\27\1\127\15\27\25\0"+
    "\6\27\6\0\7\27\4\0\14\27\1\130\11\27\25\0"+
    "\6\27\6\0\7\27\4\0\7\27\1\131\16\27\36\0"+
    "\1\132\50\0\2\57\1\0\7\57\1\0\67\57\17\0"+
    "\1\133\102\0\6\70\7\0\1\74\1\134\1\0\1\135"+
    "\13\0\1\74\1\134\2\0\1\136\21\0\2\72\3\0"+
    "\74\72\14\0\1\137\1\0\1\137\2\0\6\140\74\0"+
    "\6\141\6\0\3\141\11\0\6\141\55\0\1\142\101\0"+
    "\1\143\70\0\6\27\6\0\7\27\4\0\14\27\1\144"+
    "\11\27\25\0\6\27\6\0\7\27\4\0\1\27\1\145"+
    "\24\27\25\0\6\27\6\0\7\27\4\0\13\27\1\146"+
    "\12\27\25\0\6\27\6\0\7\27\4\0\21\27\1\147"+
    "\4\27\25\0\6\27\6\0\7\27\4\0\3\27\1\150"+
    "\22\27\25\0\6\27\6\0\7\27\4\0\21\27\1\151"+
    "\4\27\25\0\6\27\6\0\7\27\4\0\15\27\1\152"+
    "\10\27\25\0\6\27\6\0\7\27\4\0\11\27\1\153"+
    "\14\27\25\0\6\27\6\0\7\27\4\0\1\27\1\154"+
    "\12\27\1\155\11\27\25\0\6\27\6\0\7\27\4\0"+
    "\13\27\1\156\12\27\25\0\6\27\6\0\7\27\4\0"+
    "\11\27\1\157\14\27\25\0\6\27\6\0\7\27\4\0"+
    "\20\27\1\160\5\27\25\0\6\27\6\0\7\27\4\0"+
    "\12\27\1\161\13\27\25\0\6\27\6\0\7\27\4\0"+
    "\1\162\25\27\25\0\6\27\6\0\7\27\4\0\20\27"+
    "\1\163\5\27\25\0\6\27\6\0\7\27\4\0\5\27"+
    "\1\164\20\27\25\0\6\27\6\0\7\27\4\0\16\27"+
    "\1\165\7\27\25\0\6\27\6\0\7\27\4\0\21\27"+
    "\1\166\4\27\25\0\6\27\6\0\7\27\4\0\13\27"+
    "\1\167\12\27\25\0\6\27\6\0\7\27\4\0\10\27"+
    "\1\170\15\27\25\0\6\27\6\0\7\27\4\0\5\27"+
    "\1\171\20\27\43\0\1\172\120\0\1\172\44\0\6\140"+
    "\74\0\6\140\10\0\1\134\1\0\1\135\14\0\1\134"+
    "\2\0\1\136\41\0\6\141\6\0\3\141\2\0\1\75"+
    "\6\0\6\141\12\0\1\75\31\0\6\27\6\0\7\27"+
    "\4\0\11\27\1\173\14\27\25\0\6\27\6\0\7\27"+
    "\4\0\17\27\1\174\6\27\25\0\6\27\6\0\7\27"+
    "\4\0\17\27\1\175\6\27\25\0\6\27\6\0\7\27"+
    "\4\0\2\27\1\176\23\27\25\0\6\27\6\0\7\27"+
    "\4\0\7\27\1\177\16\27\25\0\6\27\6\0\7\27"+
    "\4\0\12\27\1\200\13\27\25\0\6\27\6\0\7\27"+
    "\4\0\1\27\1\201\24\27\25\0\6\27\6\0\7\27"+
    "\4\0\17\27\1\166\6\27\25\0\6\27\6\0\7\27"+
    "\4\0\20\27\1\202\5\27\25\0\6\27\6\0\7\27"+
    "\4\0\1\27\1\203\24\27\25\0\6\27\6\0\7\27"+
    "\4\0\3\27\1\204\22\27\25\0\6\27\6\0\7\27"+
    "\4\0\6\27\1\205\17\27\25\0\1\27\1\206\2\27"+
    "\1\207\1\210\6\0\7\27\4\0\5\27\1\211\20\27"+
    "\25\0\6\27\6\0\7\27\4\0\5\27\1\212\20\27"+
    "\25\0\6\27\6\0\7\27\4\0\4\27\1\213\21\27"+
    "\25\0\6\27\6\0\7\27\4\0\3\27\1\214\22\27"+
    "\25\0\6\27\6\0\7\27\4\0\21\27\1\215\4\27"+
    "\25\0\6\27\6\0\7\27\4\0\5\27\1\216\20\27"+
    "\25\0\6\27\6\0\7\27\4\0\20\27\1\217\5\27"+
    "\25\0\6\27\6\0\7\27\4\0\4\27\1\220\21\27"+
    "\25\0\6\27\6\0\7\27\4\0\16\27\1\221\7\27"+
    "\25\0\6\27\6\0\7\27\4\0\17\27\1\222\6\27"+
    "\25\0\6\27\6\0\7\27\4\0\20\27\1\223\5\27"+
    "\25\0\6\27\6\0\7\27\4\0\11\27\1\224\14\27"+
    "\25\0\6\27\6\0\7\27\4\0\13\27\1\225\12\27"+
    "\25\0\6\27\6\0\7\27\4\0\20\27\1\226\5\27"+
    "\25\0\6\27\6\0\7\27\4\0\20\27\1\227\5\27"+
    "\25\0\4\27\1\230\1\27\6\0\7\27\4\0\26\27"+
    "\25\0\3\27\1\231\2\27\6\0\7\27\4\0\26\27"+
    "\25\0\6\27\6\0\7\27\4\0\1\232\25\27\25\0"+
    "\6\27\6\0\7\27\4\0\16\27\1\233\7\27\25\0"+
    "\6\27\6\0\7\27\4\0\17\27\1\234\6\27\25\0"+
    "\6\27\6\0\7\27\4\0\10\27\1\235\15\27\25\0"+
    "\6\27\6\0\7\27\4\0\10\27\1\236\15\27\25\0"+
    "\6\27\6\0\7\27\4\0\3\27\1\237\22\27\25\0"+
    "\1\27\1\240\2\27\1\241\1\242\6\0\7\27\4\0"+
    "\26\27\25\0\6\27\6\0\7\27\4\0\5\27\1\243"+
    "\20\27\25\0\6\27\6\0\7\27\4\0\5\27\1\244"+
    "\20\27\25\0\6\27\6\0\7\27\4\0\4\27\1\245"+
    "\21\27\25\0\6\27\6\0\7\27\4\0\25\27\1\246"+
    "\25\0\6\27\6\0\7\27\4\0\1\247\25\27\25\0"+
    "\6\27\6\0\7\27\4\0\1\250\25\27\25\0\6\27"+
    "\6\0\7\27\4\0\20\27\1\251\5\27\25\0\6\27"+
    "\6\0\7\27\4\0\6\27\1\252\17\27\25\0\6\27"+
    "\6\0\7\27\4\0\15\27\1\253\10\27\25\0\6\27"+
    "\6\0\7\27\4\0\6\27\1\254\17\27\25\0\6\27"+
    "\6\0\7\27\4\0\17\27\1\255\6\27\25\0\6\27"+
    "\6\0\7\27\4\0\20\27\1\256\5\27\25\0\4\27"+
    "\1\257\1\27\6\0\7\27\4\0\26\27\25\0\3\27"+
    "\1\260\2\27\6\0\7\27\4\0\26\27\25\0\6\27"+
    "\6\0\7\27\4\0\1\261\25\27\25\0\6\27\6\0"+
    "\7\27\4\0\15\27\1\262\10\27\25\0\6\27\6\0"+
    "\7\27\4\0\20\27\1\263\5\27\25\0\6\27\6\0"+
    "\7\27\4\0\20\27\1\264\5\27\25\0\6\27\6\0"+
    "\7\27\4\0\1\27\1\265\24\27\25\0\6\27\6\0"+
    "\7\27\4\0\1\27\1\266\24\27\25\0\6\27\6\0"+
    "\7\27\4\0\6\27\1\267\17\27\25\0\6\27\6\0"+
    "\7\27\4\0\10\27\1\270\15\27\25\0\6\27\6\0"+
    "\7\27\4\0\1\271\25\27\25\0\6\27\6\0\7\27"+
    "\4\0\1\272\25\27\25\0\6\27\6\0\7\27\4\0"+
    "\20\27\1\273\5\27\25\0\6\27\6\0\7\27\4\0"+
    "\5\27\1\274\20\27\25\0\6\27\6\0\7\27\4\0"+
    "\3\27\1\275\22\27\25\0\6\27\6\0\7\27\4\0"+
    "\3\27\1\276\22\27\25\0\6\27\6\0\7\27\4\0"+
    "\14\27\1\277\11\27\25\0\6\27\6\0\7\27\4\0"+
    "\20\27\1\300\5\27\25\0\6\27\6\0\7\27\4\0"+
    "\20\27\1\301\5\27\25\0\6\27\6\0\7\27\4\0"+
    "\5\27\1\302\20\27\25\0\6\27\6\0\7\27\4\0"+
    "\5\27\1\303\20\27\25\0\6\27\6\0\7\27\4\0"+
    "\13\27\1\304\12\27\5\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[9240];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\4\1\2\11\2\1\1\11\6\1\1\11"+
    "\1\1\1\11\2\1\1\11\1\1\1\11\20\1\1\11"+
    "\1\1\2\11\1\1\1\11\1\1\6\11\1\1\1\11"+
    "\1\1\1\11\1\0\1\11\1\0\1\11\3\0\1\1"+
    "\1\11\25\1\3\11\3\0\2\1\2\11\26\1\1\11"+
    "\112\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[196];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** Number of newlines encountered up to the start of the matched text. */
  @SuppressWarnings("unused")
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  @SuppressWarnings("unused")
  protected int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  @SuppressWarnings("unused")
  private boolean zzEOFDone;

  /* user code: */
    public boolean afterStorageType = false;
    public boolean afterType = false;
    public List<String> userDefinedTypes = new ArrayList<>();
    public SlangLexer() {
      this((java.io.Reader)null);
    }

    private IElementType DebugPrint(IElementType elementType)
    {
        System.out.println("Tokenized '%s' to '%s', with state %s afterType=%b afterStruct=%b".formatted(
                yytext().toString(),
                elementType.getDebugName(),
                yystate(),
                afterType,
                afterStorageType));
        return elementType;
    }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public SlangLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      {@code false}, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position {@code pos} from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException
  {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { return BAD_CHARACTER;
            }
          // fall through
          case 74: break;
          case 2:
            { return WHITE_SPACE;
            }
          // fall through
          case 75: break;
          case 3:
            { return MOD_OP;
            }
          // fall through
          case 76: break;
          case 4:
            { return BITWISE_AND_OP;
            }
          // fall through
          case 77: break;
          case 5:
            { afterType = false; return LEFT_PAREN;
            }
          // fall through
          case 78: break;
          case 6:
            { afterType = false; return RIGHT_PAREN;
            }
          // fall through
          case 79: break;
          case 7:
            { return MUL_OP;
            }
          // fall through
          case 80: break;
          case 8:
            { return ADD_OP;
            }
          // fall through
          case 81: break;
          case 9:
            { afterType = false; return COMMA;
            }
          // fall through
          case 82: break;
          case 10:
            { return SUB_OP;
            }
          // fall through
          case 83: break;
          case 11:
            { return DIV_OP;
            }
          // fall through
          case 84: break;
          case 12:
            { return INT_LITERAL;
            }
          // fall through
          case 85: break;
          case 13:
            { afterType = false; return COLON;
            }
          // fall through
          case 86: break;
          case 14:
            { afterType = false; return SEMICOLON;
            }
          // fall through
          case 87: break;
          case 15:
            { return LESS_OP;
            }
          // fall through
          case 88: break;
          case 16:
            { afterType = false; return ASSIGN;
            }
          // fall through
          case 89: break;
          case 17:
            { return GREATER_OP;
            }
          // fall through
          case 90: break;
          case 18:
            { if (!afterType && !afterStorageType && userDefinedTypes.contains(yytext().toString()))
                {
                    afterType = true;
                    //return USER_TYPE_NAME;
                }
                else if (afterStorageType)
                {
                    afterStorageType = false;
                    userDefinedTypes.add(yytext().toString());
                }
                return IDENTIFIER;
            }
          // fall through
          case 91: break;
          case 19:
            { return LEFT_BRACKET;
            }
          // fall through
          case 92: break;
          case 20:
            { return RIGHT_BRACKET;
            }
          // fall through
          case 93: break;
          case 21:
            { return BITWISE_XOR_OP;
            }
          // fall through
          case 94: break;
          case 22:
            { afterStorageType = false; return LEFT_BRACE;
            }
          // fall through
          case 95: break;
          case 23:
            { return BITWISE_OR_OP;
            }
          // fall through
          case 96: break;
          case 24:
            { return RIGHT_BRACE;
            }
          // fall through
          case 97: break;
          case 25:
            { return BITWISE_NOT_OP;
            }
          // fall through
          case 98: break;
          case 26:
            { return MULTILINE_COMMENT;
            }
          // fall through
          case 99: break;
          case 27:
            { return COMPLETION_REQUEST;
            }
          // fall through
          case 100: break;
          case 28:
            { return MOD_ASSIGN;
            }
          // fall through
          case 101: break;
          case 29:
            { return AND_ASSIGN;
            }
          // fall through
          case 102: break;
          case 30:
            { return MUL_ASSIGN;
            }
          // fall through
          case 103: break;
          case 31:
            { return ADD_ASSIGN;
            }
          // fall through
          case 104: break;
          case 32:
            { return SUB_ASSIGN;
            }
          // fall through
          case 105: break;
          case 33:
            { return FLOAT_LITERAL;
            }
          // fall through
          case 106: break;
          case 34:
            { yybegin(MULTILINE_COMMAND_STATE); return MULTILINE_COMMENT;
            }
          // fall through
          case 107: break;
          case 35:
            { return LINE_COMMENT;
            }
          // fall through
          case 108: break;
          case 36:
            { return DIV_ASSIGN;
            }
          // fall through
          case 109: break;
          case 37:
            { return UINT_LITERAL;
            }
          // fall through
          case 110: break;
          case 38:
            { return SCOPE;
            }
          // fall through
          case 111: break;
          case 39:
            { return XOR_ASSIGN;
            }
          // fall through
          case 112: break;
          case 40:
            { return IN;
            }
          // fall through
          case 113: break;
          case 41:
            { return OR_ASSIGN;
            }
          // fall through
          case 114: break;
          case 42:
            { yybegin(YYINITIAL); return MULTILINE_COMMENT;
            }
          // fall through
          case 115: break;
          case 43:
            { return LEFT_SHIFT_ASSIGN;
            }
          // fall through
          case 116: break;
          case 44:
            { return RIGHT_SHIFT_ASSIGN;
            }
          // fall through
          case 117: break;
          case 45:
            { afterType = true; return INT32;
            }
          // fall through
          case 118: break;
          case 46:
            { return OUT;
            }
          // fall through
          case 119: break;
          case 47:
            { return DOUBLE_LITERAL;
            }
          // fall through
          case 120: break;
          case 48:
            { afterType = true; return BOOL;
            }
          // fall through
          case 121: break;
          case 49:
            { return EACH;
            }
          // fall through
          case 122: break;
          case 50:
            { afterStorageType = true; return ENUM;
            }
          // fall through
          case 123: break;
          case 51:
            { return FLAT;
            }
          // fall through
          case 124: break;
          case 52:
            { afterType = true; return HALF;
            }
          // fall through
          case 125: break;
          case 53:
            { return BOOL_LITERAL;
            }
          // fall through
          case 126: break;
          case 54:
            { afterType = true; return UINT32;
            }
          // fall through
          case 127: break;
          case 55:
            { afterType = true; return VOID;
            }
          // fall through
          case 128: break;
          case 56:
            { afterStorageType = true; return CLASS;
            }
          // fall through
          case 129: break;
          case 57:
            { return CONST;
            }
          // fall through
          case 130: break;
          case 58:
            { afterType = true; return FLOAT;
            }
          // fall through
          case 131: break;
          case 59:
            { return WHERE;
            }
          // fall through
          case 132: break;
          case 60:
            { afterType = true; return DOUBLE;
            }
          // fall through
          case 133: break;
          case 61:
            { return EXPAND;
            }
          // fall through
          case 134: break;
          case 62:
            { afterType = true; return INT8;
            }
          // fall through
          case 135: break;
          case 63:
            { afterStorageType = true; return STRUCT;
            }
          // fall through
          case 136: break;
          case 64:
            { afterType = true; return INT16;
            }
          // fall through
          case 137: break;
          case 65:
            { afterType = true; return INT64;
            }
          // fall through
          case 138: break;
          case 66:
            { return NO_DIFF;
            }
          // fall through
          case 139: break;
          case 67:
            { afterType = true; return UINT8;
            }
          // fall through
          case 140: break;
          case 68:
            { return FUNCTYPE;
            }
          // fall through
          case 141: break;
          case 69:
            { afterType = true; return UINT16;
            }
          // fall through
          case 142: break;
          case 70:
            { afterType = true; return UINT64;
            }
          // fall through
          case 143: break;
          case 71:
            { afterStorageType = true; return INTERFACE;
            }
          // fall through
          case 144: break;
          case 72:
            { afterStorageType = true; return NAMESPACE;
            }
          // fall through
          case 145: break;
          case 73:
            { return PRECISION;
            }
          // fall through
          case 146: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
