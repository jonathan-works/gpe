using System;
using System.Collections;
using System.Text;
using System.Data.SqlClient;
using System.Data.SqlTypes;
using System.IO;
using Microsoft.SqlServer.Server;
using System.Text.RegularExpressions;

//Código retirado de https://msdn.microsoft.com/pt-br/library/ff878119.aspx

[Serializable]
[Microsoft.SqlServer.Server.SqlUserDefinedAggregate(
	Microsoft.SqlServer.Server.Format.UserDefined, //use clr serialization to serialize the intermediate result  
	IsInvariantToNulls = true,//optimizer property  
	IsInvariantToDuplicates = false,//optimizer property  
	IsInvariantToOrder = false,//optimizer property  
	MaxByteSize = -1)//maximum size in bytes of persisted value  
]
public class Concatenate : Microsoft.SqlServer.Server.IBinarySerialize
{
	/// <summary>  
	/// The variable that holds the intermediate result of the concatenation  
	/// </summary>  
	private StringBuilder IntermediateResult;

	/// <summary>  
	/// Initialize the internal data structures  
	/// </summary>  
	public void Init()
	{
		this.IntermediateResult = new StringBuilder();
	}

	/// <summary>  
	/// Accumulate the next value, nop if the value is null  
	/// </summary>  
	/// <param name="value"></param>  
	/// <param name="delimiter"></param> 
	/// <param name="repeated"></param> 
	public void Accumulate(SqlString value, SqlString delimiter, SqlBoolean repeated)
	{
		if ( value.IsNull )
		{
			return;
		}
		if ( delimiter.IsNull )
		{
			delimiter = ",";
		}
		if (this.IntermediateResult.Length == 0)
		{
			this.IntermediateResult.Append(value.ToString());
		}
		else
		{
			if (repeated.IsFalse)
			{
				if (!this.IntermediateResult.ToString().Contains(value.ToString()))
				{
					this.IntermediateResult.Append(String.Concat(delimiter, value.ToString()));
				}
			}
			else
			{
				this.IntermediateResult.Append(String.Concat(delimiter, value.ToString()));	
			}
		}

	}


	/// <summary>  
	/// Merge the partially computed aggregate with this aggregate.  
	/// </summary>  
	/// <param name="other"></param>  
	public void Merge(Concatenate other)
	{
		this.IntermediateResult.Append(other.IntermediateResult);
	}

	/// <summary>  
	/// Called at the end of aggregation, to return the results of the aggregation  
	/// </summary>  
	/// <returns></returns>  
	public SqlString Terminate()
	{
		return new SqlString(this.IntermediateResult.ToString());
	}

	public void Read(BinaryReader r)
	{
		if (r == null) throw new ArgumentNullException("r");
		this.IntermediateResult = new StringBuilder(r.ReadString());
	}

	public void Write(BinaryWriter w)
	{
		if (w == null) throw new ArgumentNullException("w");
		w.Write(this.IntermediateResult.ToString());
	}
}

public sealed class RegularExpression
{
	private RegularExpression()
	{

	}

	/// <summary>  
	///     This method performs a pattern based substitution based on the provided input string, pattern  
	///     string, and replacement string.  
	/// </summary>  
	/// <param name="sqlInput">The source material</param>  
	/// <param name="sqlPattern">How to parse the source material</param>  
	/// <param name="sqlReplacement">What the output should look like</param>  
	/// <returns></returns>  
	public static string Replace(SqlString sqlInput, SqlString sqlPattern, SqlString sqlReplacement)
	{
		string input = (sqlInput.IsNull) ? string.Empty : sqlInput.Value;
		string pattern = (sqlPattern.IsNull) ? string.Empty : sqlPattern.Value;
		string replacement = (sqlReplacement.IsNull) ? string.Empty : sqlReplacement.Value;
		return Regex.Replace(input, pattern, replacement);
	}
}

//Código retirado de http://sqlblog.com/blogs/adam_machanic/archive/2009/04/28/sqlclr-string-splitting-part-2-even-faster-even-more-scalable.aspx
public partial class UserDefinedFunctions
{
    [Microsoft.SqlServer.Server.SqlFunction(
       FillRowMethodName = "FillRow_Multi",
       TableDefinition = "item nvarchar(4000)"
       )
    ]
    public static IEnumerator SplitString_Multi(
      [SqlFacet(MaxSize = -1)]
      SqlChars Input,
      [SqlFacet(MaxSize = 255)]
      SqlChars Delimiter
       )
    {
        return (
            (Input.IsNull || Delimiter.IsNull) ?
            new SplitStringMulti(new char[0], new char[0]) :
            new SplitStringMulti(Input.Value, Delimiter.Value));
    }

    public static void FillRow_Multi(object obj, out SqlString item)
    {
        item = new SqlString((string)obj);
    }

    public class SplitStringMulti : IEnumerator
    {
        public SplitStringMulti(char[] TheString, char[] Delimiter)
        {
            theString = TheString;
            stringLen = TheString.Length;
            delimiter = Delimiter;
            delimiterLen = (byte)(Delimiter.Length);
            isSingleCharDelim = (delimiterLen == 1);

            lastPos = 0;
            nextPos = delimiterLen * -1;
        }

        #region IEnumerator Members

        public object Current
        {
            get
            {
				return new string(theString, lastPos, nextPos - lastPos).Trim();
            }
        }

        public bool MoveNext()
        {
            if (nextPos >= stringLen)
                return false;
            else
            {
                lastPos = nextPos + delimiterLen;

                for (int i = lastPos; i < stringLen; i++)
                {
                    bool matches = true;

                    //Optimize for single-character delimiters
                    if (isSingleCharDelim)
                    {
                        if (theString[i] != delimiter[0])
                            matches = false;
                    }
                    else
                    {
                        for (byte j = 0; j < delimiterLen; j++)
                        {
                            if (((i + j) >= stringLen) || (theString[i + j] != delimiter[j]))
                            {
                                matches = false;
                                break;
                            }
                        }
                    }

                    if (matches)
                    {
                        nextPos = i;

                        //Deal with consecutive delimiters
                        if ((nextPos - lastPos) > 0)
                            return true;
                        else
                        {
                            i += (delimiterLen-1);
                            lastPos += delimiterLen;
                        }
                    }
                }

                lastPos = nextPos + delimiterLen;
                nextPos = stringLen;

                if ((nextPos - lastPos) > 0)
                    return true;
                else
                    return false;
            }
        }

        public void Reset()
        {
            lastPos = 0;
            nextPos = delimiterLen * -1;
        }

        #endregion

        private int lastPos;
        private int nextPos;

        private readonly char[] theString;
        private readonly char[] delimiter;
        private readonly int stringLen;
        private readonly byte delimiterLen;
        private readonly bool isSingleCharDelim;
    }
};
