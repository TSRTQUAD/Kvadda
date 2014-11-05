%nodes = getPolygonGrid(lon,lat,ppa) returns points that are within a
%concave or convex polygon using the inpolygon function.

%lon and lat are columns representing the vertices of the polygon, as used
%in the Matlab function inpolygon

%ppa refers to the points per unit area you would like inside the polygon.
%Here unit area refers to a 1.0 X 1.0 square in the axes.

function [nodes] = getPolygonGrid( object, ppa)
% Extract all coordinates from the areas to be searched
lon = []; lat = [];
nrofareas = length(object.area);
for ii = 1:nrofareas
    lat = [lat;object.area{ii}(:,1)];
    lon = [lon;object.area{ii}(:,2)];
end
N = sqrt(ppa);

% Find the bounding rectangle
lower_lat = min(lat);
higher_lat = max(lat);
lower_lon = min(lon);
higher_lon = max(lon);

% Create a grid of points within the bounding rectangle
inc_lat = 1/N;
inc_lon = 1/N;
interval_lat = lower_lat:inc_lat:higher_lat;
interval_lon = lower_lon:inc_lon:higher_lon;
[bigGridLat, bigGridLon] = meshgrid(interval_lat, interval_lon);

%Filter grid to get only points in polygons
nodes = [];
for ii = 1:nrofareas
    lat = object.area{ii}(:,1);
    lon = object.area{ii}(:,2);
    inallowed = inpolygon(bigGridLat(:), bigGridLon(:), lat, lon);
    for jj = 1:length(object.forbiddenarea)
        latforbidden = object.forbiddenarea{jj}(:,1);
        lonforbidden = object.forbiddenarea{jj}(:,2);
        inforbidden = inpolygon(bigGridLat(:), bigGridLon(:),...
            latforbidden, lonforbidden);
        for kk = 1:length(inallowed)
            if inforbidden(kk) == 1
                inallowed(kk) = 0;
            end
        end
    end
    nodes = [nodes;[bigGridLat(inallowed) bigGridLon(inallowed)]];
end

end