package com.janboerman.f2pstarassist.common;

public enum StarTier {

    SIZE_1,
    SIZE_2,
    SIZE_3,
    SIZE_4,
    SIZE_5,
    SIZE_6,
    SIZE_7,
    SIZE_8,
    SIZE_9;

    private static final StarTier[] VALUES = new StarTier[] {SIZE_1, SIZE_2, SIZE_3, SIZE_4, SIZE_5, SIZE_6, SIZE_7, SIZE_8, SIZE_9};

    public int getSize() {
        return ordinal() + 1;
    }

    @Override
    public String toString() {
        return "size-" + getSize();
    }

    public int getRequiredMiningLevel() {
        return getSize() * 10;
    }

    public StarTier oneLess() {
        switch (this) {
            case SIZE_1: return null;
            default: return VALUES[ordinal() - 1];
        }
    }

    public static StarTier bySize(int size) {
        if (size <= 0 || size > 9)
            throw new IllegalArgumentException("size must be in range [1-9]");

        return VALUES[size - 1];
    }
}
