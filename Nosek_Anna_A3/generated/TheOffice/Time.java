//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.5
//
// <auto-generated>
//
// Generated from file `office.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package TheOffice;

public class Time implements java.lang.Cloneable,
                             java.io.Serializable
{
    public int hour;

    public int minute;

    public int second;

    public Time()
    {
    }

    public Time(int hour, int minute, int second)
    {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public boolean equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        Time r = null;
        if(rhs instanceof Time)
        {
            r = (Time)rhs;
        }

        if(r != null)
        {
            if(this.hour != r.hour)
            {
                return false;
            }
            if(this.minute != r.minute)
            {
                return false;
            }
            if(this.second != r.second)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int hashCode()
    {
        int h_ = 5381;
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, "::TheOffice::Time");
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, hour);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, minute);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, second);
        return h_;
    }

    public Time clone()
    {
        Time c = null;
        try
        {
            c = (Time)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public void ice_writeMembers(com.zeroc.Ice.OutputStream ostr)
    {
        ostr.writeInt(this.hour);
        ostr.writeInt(this.minute);
        ostr.writeInt(this.second);
    }

    public void ice_readMembers(com.zeroc.Ice.InputStream istr)
    {
        this.hour = istr.readInt();
        this.minute = istr.readInt();
        this.second = istr.readInt();
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, Time v)
    {
        if(v == null)
        {
            _nullMarshalValue.ice_writeMembers(ostr);
        }
        else
        {
            v.ice_writeMembers(ostr);
        }
    }

    static public Time ice_read(com.zeroc.Ice.InputStream istr)
    {
        Time v = new Time();
        v.ice_readMembers(istr);
        return v;
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, java.util.Optional<Time> v)
    {
        if(v != null && v.isPresent())
        {
            ice_write(ostr, tag, v.get());
        }
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, Time v)
    {
        if(ostr.writeOptional(tag, com.zeroc.Ice.OptionalFormat.VSize))
        {
            ostr.writeSize(12);
            ice_write(ostr, v);
        }
    }

    static public java.util.Optional<Time> ice_read(com.zeroc.Ice.InputStream istr, int tag)
    {
        if(istr.readOptional(tag, com.zeroc.Ice.OptionalFormat.VSize))
        {
            istr.skipSize();
            return java.util.Optional.of(Time.ice_read(istr));
        }
        else
        {
            return java.util.Optional.empty();
        }
    }

    private static final Time _nullMarshalValue = new Time();

    /** @hidden */
    public static final long serialVersionUID = -6285495075686405442L;
}
