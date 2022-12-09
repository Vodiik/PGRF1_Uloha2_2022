package solids;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    List<Solid> solids = new ArrayList<>();

    public List<Solid> getSolids() {
        return solids;
    }

    public void addSolid(Solid solid){
        solids.add(solid);
    }
}
